package com.aigen.junitgen.ai;

import com.aigen.junitgen.model.AIInput;

import java.util.List;

public class OpenAIPromptBuilder {

    public String buildPrompt(AIInput input) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are a expert senior Java developer writing high-quality unit tests.\n")
                .append("Given the following Java class, generate a JUnit 5 test class for it.\n\n");

        if (!input.publicMethods.isEmpty()) {
            prompt.append("Focus on covering these public methods with meaningful, scenario-driven test cases:\n");
            for (String method : input.publicMethods) {
                prompt.append("- ").append(method).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("Use best practices such as:\n")
                .append("- Descriptive test method names like\n")
                .append("    • shouldReturnX_whenY\n")
                .append("    • shouldThrowException_whenInvalidInput\n")
                .append("- `@BeforeEach` setup with `@InjectMocks` and `@Mock` using Mockito\n")
                .append("- Use of `when(...).thenReturn(...)` for mocking behavior\n")
                .append("- Use of `assertEquals`, `assertThrows`, etc. or other relevant assertions\n")
                .append("- One test per logical behavior or scenario\n\n");

        prompt.append("Here is the class to test:\n")
                .append("```\n")
                .append(input.fullSourceCode)
                .append("\n```\n");

        prompt.append("Return ONLY the test class code. Do not include any explanation or comments outside the code block.\n");

        return prompt.toString();
    }
}
