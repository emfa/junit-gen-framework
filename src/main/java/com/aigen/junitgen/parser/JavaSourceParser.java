package com.aigen.junitgen.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSourceParser {

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^\\s*package\\s+([a-zA-Z0-9_.]+);");
    private static final Pattern CLASS_PATTERN = Pattern.compile("public\\s+(?:class|interface|record|enum)\\s+(\\w+)");
    private static final Pattern METHOD_PATTERN = Pattern.compile("public\\s+[^=\\n]*\\s+(\\w+)\\s*\\([^)]*\\)\\s*(?:\\{|throws)");

    public ParsedJavaFile parse(Path filePath) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        String content = String.join("\n", lines);

        String packageName = extractFirstMatch(PACKAGE_PATTERN, content).orElse("");
        String className = extractFirstMatch(CLASS_PATTERN, content).orElse("UnknownClass");

        List<String> methodNames = new ArrayList<>();
        Matcher matcher = METHOD_PATTERN.matcher(content);
        while (matcher.find()) {
            methodNames.add(matcher.group(1));
        }

        return new ParsedJavaFile(packageName, className, methodNames);
    }

    private Optional<String> extractFirstMatch(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }

    // Inner data class
    public static class ParsedJavaFile {
        public final String packageName;
        public final String className;
        public final List<String> publicMethods;

        public ParsedJavaFile(String packageName, String className, List<String> publicMethods) {
            this.packageName = packageName;
            this.className = className;
            this.publicMethods = publicMethods;
        }
    }

}
