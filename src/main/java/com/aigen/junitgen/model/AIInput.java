package com.aigen.junitgen.model;

import java.util.List;

public class AIInput {
    public final String packageName;
    public final String className;
    public final String fullSourceCode;
    public final List<String> publicMethods;
    public final String fullyQualifiedName;

    public AIInput(String packageName, String className, String fullSourceCode, List<String> publicMethods) {
        this.packageName = packageName;
        this.className = className;
        this.fullSourceCode = fullSourceCode;
        this.publicMethods = publicMethods;
        this.fullyQualifiedName = packageName + "." + className;
    }
}