package com.aigen.junitgen.writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TestFileWriter {

    public Path writeTestFile(Path projectRoot, String packageName, String className, String testClassContent) throws IOException {
        String testClassName = className + "Test";

        // Build path like src/test/java/com/example/UserServiceTest.java
        Path testFileDir = projectRoot.resolve("src/test/java/" + packageName.replace('.', '/'));
        Path testFilePath = testFileDir.resolve(testClassName + ".java");

        // Create directories if not exist
        Files.createDirectories(testFileDir);

        // Write test content (overwrite if already exists)
        Files.writeString(testFilePath, testClassContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return testFilePath;
    }
}
