package com.aigen.junitgen.util;

public class TestOutputFormatter {

    public static String clean(String rawOutput) {
        if (rawOutput == null || rawOutput.isBlank()) return "";

        String cleaned = rawOutput;

        // Remove markdown code fences (```java ... ```)
        cleaned = cleaned.replaceAll("(?s)```(java)?", "");

        // Normalize smart quotes
        cleaned = cleaned.replace("“", "\"").replace("”", "\"")
                .replace("‘", "'").replace("’", "'");

        // Trim leading/trailing whitespace
        cleaned = cleaned.trim();

        return cleaned;
    }
}