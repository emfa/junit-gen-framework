package com.aigen.junitgen.model;

import lombok.Data;
import lombok.ToString;

import java.nio.file.Path;
import java.util.List;

@Data
@ToString
public class ClassInfo {
    public String packageName;
    public String className;
    public String fullyQualifiedName;
    public List<String> importedClasses;
    public List<String> publicMethodNames;
    public List<String> fieldNames;
    public Path filePath;
    public boolean isTestClass;

    public ClassInfo(String packageName, String className, List<String> imports,
                     List<String> publicMethods, List<String> fields,
                     Path filePath, boolean isTestClass) {
        this.packageName = packageName;
        this.className = className;
        this.importedClasses = imports;
        this.publicMethodNames = publicMethods;
        this.fieldNames = fields;
        this.filePath = filePath;
        this.isTestClass = isTestClass;
        this.fullyQualifiedName = packageName + "." + className;
    }

}