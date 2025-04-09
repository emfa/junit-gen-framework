package com.aigen.junitgen.ai;

public class MockAIService {

    public String generateTestClass(String originalSourceCode, String className, String packageName) {
        // Simulated AI output - in real version this would be generated from a real AI model

        String testClassName = className + "Test";

        StringBuilder sb = new StringBuilder();
        if (!packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }

        sb.append("import org.junit.jupiter.api.Test;\n")
                .append("import static org.junit.jupiter.api.Assertions.*;\n\n")
                .append("public class ").append(testClassName).append(" {\n\n")
                .append("    @Test\n")
                .append("    void sampleTest() {\n")
                .append("        // TODO: Replace with actual tests\n")
                .append("        fail(\"Not yet implemented\");\n")
                .append("    }\n")
                .append("}\n");

        return sb.toString();
    }
}