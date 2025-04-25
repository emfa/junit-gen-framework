package com.aigen.junitgen.cli;

import com.aigen.junitgen.ai.AITestGenerator;
import com.aigen.junitgen.ai.GeminiTestGenerator;
import com.aigen.junitgen.ai.OpenAITestGenerator;
import com.aigen.junitgen.config.JunitGenConfig;
import com.aigen.junitgen.config.JunitGenConfigLoader;
import com.aigen.junitgen.git.GitDiffService;
import com.aigen.junitgen.model.AIInput;
import com.aigen.junitgen.model.ClassInfo;
import com.aigen.junitgen.parser.JavaSourceParser;
import com.aigen.junitgen.scan.ClassIndex;
import com.aigen.junitgen.scan.ProjectScanner;
import com.aigen.junitgen.util.TestOutputFormatter;
import com.aigen.junitgen.writer.TestFileWriter;
import org.eclipse.jgit.diff.DiffEntry;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Command(name = "junitgen", mixinStandardHelpOptions = true, version = "0.1",
        description = "AI-powered JUnit test generator")
public class JUnitGenCLI implements Runnable {


    @Option(names = {"-p", "--project-path"}, required = true, description = "Path to the Git project")
    private Path projectPath;

    @Option(names = {"-d", "--diff"}, defaultValue = "staged", description = "Diff type: staged or commits")
    private String diffType;

    @Option(names = "--model", description = "AI model to use (gemini, openai)")
    String model;

    @Option(names = "--dry-run", description = "Print test content instead of writing to file")
    boolean dryRun;

    @Option(names = "--debug-prompt", description = "Prints the AI prompt before sending")
    boolean debugPrompt;

    @Option(names = "--test-overwrite", description = "What to do if test file already exists: skip, warn, or overwrite", defaultValue = "warn")
    String overwriteMode;

    @Option(names = "--file", description = "Generate test for a specific .java file (bypasses Git)")
    Path singleFile;

    @Option(names = "--folder", description = "Generate tests for all .java files in this folder (recursively)")
    Path folder;

    @Override
    public void run() {

        int totalProcessed = 0;
        int testsWritten = 0;
        int testsSkipped = 0;
        int testsErrored = 0;

        // Scan all files and build class index
        ProjectScanner scanner = new ProjectScanner(projectPath);
        ClassIndex classIndex = new ClassIndex();

        try {
            classIndex.addAll(scanner.scan());
            classIndex.printSummary();
        } catch (IOException e) {
            System.err.println("Failed to scan project: " + e.getMessage());
            return;
        }

        JunitGenConfig config = JunitGenConfigLoader.loadConfig(projectPath.toFile());

        if (model == null || model.isBlank()) {
            model = config.model;
            System.out.println("Using model from config: " + model);
        } else {
            System.out.println("Using model from CLI: " + model);
        }

        if (!dryRun && config.dryRun) {
            dryRun = true;
            System.out.println("Enabling dry-run mode from config");
        }

        System.out.println("Analyzing Git diff in: " + projectPath);
        System.out.println("Diff type selected: " + diffType);

        if (!Files.exists(projectPath.resolve(".git"))) {
            System.err.println("Not a valid Git repo: " + projectPath);
            return;
        }

        try {

            GitDiffService diffService = new GitDiffService();
            JavaSourceParser parser = new JavaSourceParser();
            AITestGenerator aiService;
            TestFileWriter testWriter = new TestFileWriter();

            switch (model.toLowerCase()) {
                case "openai":
                    aiService = new OpenAITestGenerator(System.getenv("OPENAI_API_KEY"), "gpt-3.5-turbo");
                    break;
                case "gemini":
                default:
                    aiService = new GeminiTestGenerator(System.getenv("GEMINI_API_KEY"));
                    break;
            }

            Set<Path> filesToGenerate = new HashSet<>();

            if (singleFile != null) {
                if (Files.exists(singleFile) && singleFile.toString().endsWith(".java")) {
                    filesToGenerate.add(singleFile);
                } else {
                    System.out.println("Invalid or non-java file: " + singleFile);
                    return;
                }
            } else if (folder != null) {
                try {
                    Files.walk(folder)
                            .filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".java"))
                            .filter(path -> !path.toString().contains("/test/")) // avoid test files
                            .forEach(filesToGenerate::add);
                } catch (IOException e) {
                    System.err.println("Failed to read folder: " + e.getMessage());
                    return;
                }
            } else {
                // fallback to Git mode
                List<DiffEntry> diffs = diffService.getStagedChanges(projectPath);

                List<DiffEntry> javaDiffs = diffs.stream()
                        .filter(diff -> diff.getNewPath().endsWith(".java"))
                        .toList();
                if (javaDiffs.isEmpty()) {
                    System.out.println("No staged Java files found.");
                } else {
                    System.out.println("Staged Java files found.");
                    for (DiffEntry diff : javaDiffs) {
                        Path path = projectPath.resolve(diff.getNewPath());
                        filesToGenerate.add(path);

                        JavaSourceParser.ParsedJavaFile parsed = parser.parse(path);
                        String fqn = parsed.packageName + "." + parsed.className;

                        List<ClassInfo> dependents = classIndex.findDependentsOf(fqn);
                        for (ClassInfo dep : dependents) {

                            if (dep.isTestClass) {
                                System.out.println("Skipping test-class dependency: " + dep.fullyQualifiedName);
                                continue;
                            }

                            System.out.println("Dependent class found: " + dep.fullyQualifiedName);
                            filesToGenerate.add(dep.filePath);
                        }
                    }
                }
            }

            for (Path filePath : filesToGenerate) {
                totalProcessed++;

                JavaSourceParser.ParsedJavaFile parsed = parser.parse(filePath);

                System.out.println("Package: " + parsed.packageName);
                System.out.println("Class: " + parsed.className);
                System.out.println("Methods:" + parsed.publicMethods);

                String fullSource = Files.readString(filePath);

                String testContent = aiService.generateTestClass(new AIInput(parsed.packageName, parsed.className, fullSource, parsed.publicMethods), debugPrompt, classIndex);

                if (dryRun) {
                    System.out.println("\n--- BEGIN GENERATED TEST ---\n");
                    System.out.println(testContent);
                    System.out.println("\n--- END GENERATED TEST ---\n");
                } else {
                    Path testFilePath = testWriter.getTestFilePath(projectPath, parsed.packageName, parsed.className);

                    if (Files.exists(testFilePath)) {
                        switch (overwriteMode.toLowerCase()) {
                            case "skip" -> {
                                System.out.println("⏩ Skipped existing test file: " + testFilePath.getFileName());
                                testsSkipped++;
                                continue;
                            }
                            case "warn" -> {
                                System.out.println("⚠️ Overwriting existing test file: " + testFilePath.getFileName());
                                break;
                            }
                            case "overwrite" -> {
                                break;
                            }
                            default -> {
                                System.out.println("⚠️ Unknown overwrite strategy: " + overwriteMode + ". Defaulting to 'warn'");
                                break;
                            }
                        }
                    }

                    testWriter.writeTestFile(projectPath, parsed.packageName, parsed.className, TestOutputFormatter.clean(testContent));
                    testsWritten++;
                    System.out.println("Test written to: " + testFilePath);
                }
            }

        } catch (Exception e) {
            System.err.println("Error during Git diff or parsing: " + e.getMessage());
            testsErrored++;
            e.printStackTrace();
        }

        System.out.println("\n Summary:");
        System.out.println("   Classes processed: " + totalProcessed);
        System.out.println("   Test files written: " + testsWritten);
        System.out.println("   Skipped (existing): " + testsSkipped);
        System.out.println("   Errors: " + testsErrored);

    }
}