package com.aigen.junitgen.cli;

import com.aigen.junitgen.git.GitDiffService;
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
            List<DiffEntry> diffs = diffService.getStagedChanges(projectPath);

            if (diffs.isEmpty()) {
                System.out.println("No staged changes found.");
            } else {
                System.out.println("Staged file changes:");
                diffs.stream().filter(diffEntry -> diffEntry.getNewPath().endsWith(".java"))
                        .forEach(diffEntry -> System.out.printf("  %-6s %s%n", diffEntry.getChangeType(), diffEntry.getNewPath()));
            }
        } catch (Exception e) {
            System.err.println("Error during Git diff: " + e.getMessage());
            e.printStackTrace();
        }
    }

}