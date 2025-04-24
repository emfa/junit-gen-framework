package com.aigen.junitgen.scan;

import com.aigen.junitgen.model.ClassInfo;

import java.util.*;
import java.util.stream.Collectors;

public class ClassIndex {

    private final Map<String, ClassInfo> classMap = new HashMap<>();

    public void add(ClassInfo classInfo) {
        classMap.put(classInfo.fullyQualifiedName, classInfo);
    }

    public void addAll(List<ClassInfo> classes) {
        for (ClassInfo ci : classes) {
            add(ci);
        }
    }

    public Optional<ClassInfo> get(String fullyQualifiedName) {
        return Optional.ofNullable(classMap.get(fullyQualifiedName));
    }

    public List<ClassInfo> getAll() {
        return new ArrayList<>(classMap.values());
    }

    public List<ClassInfo> getAllTestClasses() {
        return classMap.values().stream()
                .filter(ClassInfo::isTestClass)
                .collect(Collectors.toList());
    }

    public List<ClassInfo> getAllSourceClasses() {
        return classMap.values().stream()
                .filter(ci -> !ci.isTestClass)
                .collect(Collectors.toList());
    }

    public boolean containsTestFor(String className) {
        return classMap.values().stream()
                .anyMatch(ci -> ci.isTestClass && ci.className.equals(className + "Test"));
    }

    public Set<String> allImportedTypes() {
        return classMap.values().stream()
                .flatMap(ci -> ci.importedClasses.stream())
                .collect(Collectors.toSet());
    }

    public List<ClassInfo> findDependentsOf(String targetClass) {
        return classMap.values().stream()
                .filter(ci -> ci.importedClasses.contains(targetClass))
                .collect(Collectors.toList());
    }

    public void printSummary() {
        System.out.println("📦 Class Index Summary:");
        System.out.println("  - Total classes: " + classMap.size());
        System.out.println("  - Source classes: " + getAllSourceClasses().size());
        System.out.println("  - Test classes: " + getAllTestClasses().size());
    }
}