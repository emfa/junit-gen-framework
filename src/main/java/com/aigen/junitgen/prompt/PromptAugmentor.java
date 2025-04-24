package com.aigen.junitgen.prompt;

import com.aigen.junitgen.model.AIInput;
import com.aigen.junitgen.scan.ClassIndex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PromptAugmentor {

    public static String enrichPrompt(AIInput input, ClassIndex index, String basePrompt) {
        StringBuilder enriched = new StringBuilder(basePrompt);

        Optional<List<String>> usedImports = index.get(input.fullyQualifiedName)
                .map(ci -> ci.importedClasses);

        usedImports.ifPresent(imports -> {
            for (String importClass : imports) {
                index.get(importClass).ifPresent(dep -> {
                    enriched.append("\n\nHere is the definition of class ")
                            .append(dep.className)
                            .append(":\n```\n")
                            .append(readFile(dep.filePath))
                            .append("\n```");
                });
            }
        });

        return enriched.toString();
    }

    private static String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "// Could not read: " + path.getFileName();
        }
    }
}