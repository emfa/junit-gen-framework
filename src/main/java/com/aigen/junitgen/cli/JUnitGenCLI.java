package com.aigen.junitgen.cli;

import com.aigen.junitgen.ai.MockAIService;
import com.aigen.junitgen.git.GitDiffService;
import com.aigen.junitgen.parser.JavaSourceParser;
import com.aigen.junitgen.writer.TestFileWriter;
import org.eclipse.jgit.diff.DiffEntry;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Command(name = "junitgen", mixinStandardHelpOptions = true, version = "0.1",
        description = "AI-powered JUnit test generator")
public class JUnitGenCLI implements Runnable{


    @Option(names = {"-p", "--project-path"}, required = true, description = "Path to the Git project")
    private Path projectPath;

    @Option(names = {"-d", "--diff"}, defaultValue = "staged", description = "Diff type: staged or commits")
    private String diffType;

    @Override
    public void run() {
        System.out.println("Analyzing Git diff in: " + projectPath);
        System.out.println("Diff type selected: " + diffType);

        if (!Files.exists(projectPath.resolve(".git"))) {
            System.err.println("Not a valid Git repo: " + projectPath);
            return;
        }

        try {
            GitDiffService diffService = new GitDiffService();
            JavaSourceParser parser = new JavaSourceParser();
            MockAIService aiService = new MockAIService();
            TestFileWriter testWriter = new TestFileWriter();

            List<DiffEntry> diffs = diffService.getStagedChanges(projectPath);

            List<DiffEntry> javaDiffs = diffs.stream()
                    .filter(diff -> diff.getNewPath().endsWith(".java"))
                    .toList();

            if (javaDiffs.isEmpty()) {
                System.out.println("No staged Java files found.");
            } else {
                System.out.println("Staged Java file changes:");
                for (DiffEntry diff : javaDiffs) {
                    System.out.printf("  %-6s %s%n", diff.getChangeType(), diff.getNewPath());

                    // Resolve file path in project dir
                    Path filePath = projectPath.resolve(diff.getNewPath());
                    JavaSourceParser.ParsedJavaFile parsed = parser.parse(filePath);

                    System.out.println("Package: " + parsed.packageName);
                    System.out.println("Class: " + parsed.className);
                    System.out.println("Methods:" + parsed.publicMethods);

                    String fullSource = Files.readString(filePath);

                    String testContent = aiService.generateTestClass(fullSource, parsed.className, parsed.packageName);

                    Path testPath = testWriter.writeTestFile(projectPath, parsed.packageName, parsed.className, testContent);

                    System.out.println("    Test written to: " + testPath);
                }
            }

        } catch (Exception e) {
            System.err.println("Error during Git diff or parsing: " + e.getMessage());
            e.printStackTrace();
        }
    }



}