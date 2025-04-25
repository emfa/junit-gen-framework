package com.aigen.junitgen.writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TestFileWriter {

    public void writeTestFile(Path projectRoot, String packageName, String className, String testClassContent) throws IOException {

        Path testFilePath = getTestFilePath(projectRoot,packageName,className);

        Path testFileDir = projectRoot.resolve("src/test/java/" + packageName.replace('.', '/'));

        // Create directories if not exist
        Files.createDirectories(testFileDir);

        // Write test content (overwrite if already exists)
        Files.writeString(testFilePath, testClassContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public Path getTestFilePath(Path projectRoot, String packageName, String className) {
        String testClassName = className + "Test";
        Path testFileDir = projectRoot.resolve("src/test/java/" + packageName.replace('.', '/'));
        return testFileDir.resolve(testClassName + ".java");
    }
}
