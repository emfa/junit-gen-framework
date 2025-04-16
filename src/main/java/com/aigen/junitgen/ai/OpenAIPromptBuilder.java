package com.aigen.junitgen.ai;

import com.aigen.junitgen.model.AIInput;

import java.util.List;

public class OpenAIPromptBuilder {

    public String buildPrompt(AIInput input) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert Java developer.\n")
                .append("Given the following Java class, generate a JUnit 5 test class for it.\n\n");

        if (!input.publicMethods.isEmpty()) {
            prompt.append("Focus on generating meaningful tests for these public methods:\n");
            for (String method : input.publicMethods) {
                prompt.append("- ").append(method).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("Class to test:\n")
                .append("```\n")
                .append(input.fullSourceCode)
                .append("\n```\n\n")
                .append("Please return only the generated test class code, and nothing else.");

        return prompt.toString();
    }
}
