package com.aigen.junitgen.ai;

import com.aigen.junitgen.model.AIInput;

import java.util.List;

public class MockTestGenerator implements AITestGenerator{

    @Override
    public String generateTestClass(AIInput input, boolean debugPrompt) {
        // Simulated AI output - in real version this would be generated from a real AI model

        String testClassName = input.className + "Test";

        StringBuilder sb = new StringBuilder();
        if (!input.packageName.isEmpty()) {
            sb.append("package ").append(input.packageName).append(";\n\n");
        }

        sb.append("import org.junit.jupiter.api.Test;\n")
                .append("import static org.junit.jupiter.api.Assertions.*;\n\n")
                .append("public class ").append(testClassName).append(" {\n\n");

        // Filter out "main" method
        List<String> filteredMethods = input.publicMethods.stream()
                .filter(m -> !"main".equals(m))
                .toList();


        if (filteredMethods.isEmpty()) {
            sb.append("    @Test\n")
                    .append("    void sampleTest() {\n")
                    .append("        fail(\"Not yet implemented\");\n")
                    .append("    }\n");
        } else {
            for (String method : filteredMethods) {
                String testName = generateTestMethodName(method);
                sb.append("    @Test\n")
                        .append("    void ").append(testName).append("() {\n")
                        .append("        // TODO: Add test for ").append(method).append("\n")
                        .append("        fail(\"Not yet implemented\");\n")
                        .append("    }\n\n");
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String generateTestMethodName(String method) {
        return "should" + capitalize(method) + "_whenValidInputsProvided";
    }

    private String capitalize(String methodName) {
        if (methodName == null || methodName.isEmpty()) return methodName;
        return Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
    }

}