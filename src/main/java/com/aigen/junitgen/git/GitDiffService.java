package com.aigen.junitgen.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GitDiffService {

    public List<DiffEntry> getStagedChanges(Path projectPath) throws IOException, GitAPIException {
        File gitDir = new File(projectPath.toFile(), ".git");
        Repository repository = new FileRepositoryBuilder()
                .setGitDir(gitDir)
                .readEnvironment()
                .findGitDir()
                .build();

        try (Git git = new Git(repository)) {
            return git.diff()
                    .setCached(true)  // <-- this compares HEAD vs staging area
                    .call();
        }

    }
}