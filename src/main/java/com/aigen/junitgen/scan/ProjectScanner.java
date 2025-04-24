package com.aigen.junitgen.scan;

import com.aigen.junitgen.model.ClassInfo;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.ImportDeclaration;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectScanner {

    private final Path mainSrc;
    private final Path testSrc;

    public ProjectScanner(Path projectRoot) {
        this.mainSrc = projectRoot.resolve("src/main/java");
        this.testSrc = projectRoot.resolve("src/test/java");
    }

    public List<ClassInfo> scan() throws IOException {
        List<ClassInfo> allClasses = new ArrayList<>();
        allClasses.addAll(scanPath(mainSrc, false));
        allClasses.addAll(scanPath(testSrc, true));
        return allClasses;
    }

    private List<ClassInfo> scanPath(Path rootPath, boolean isTestClass) throws IOException {
        if (!Files.exists(rootPath)) return Collections.emptyList();

        List<ClassInfo> result = new ArrayList<>();

        Files.walk(rootPath)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(path);

                        Optional<ClassOrInterfaceDeclaration> classDecl = cu
                                .findFirst(ClassOrInterfaceDeclaration.class);

                        if (classDecl.isPresent()) {
                            String packageName = cu.getPackageDeclaration()
                                    .map(pd -> pd.getName().toString())
                                    .orElse("");
                            String className = classDecl.get().getNameAsString();

                            List<String> imports = cu.getImports().stream()
                                    .map(ImportDeclaration::getNameAsString)
                                    .collect(Collectors.toList());

                            List<String> methods = classDecl.get().getMethods().stream()
                                    .filter(m -> m.isPublic())
                                    .map(MethodDeclaration::getNameAsString)
                                    .collect(Collectors.toList());

                            List<String> fields = classDecl.get().getFields().stream()
                                    .flatMap(f -> f.getVariables().stream())
                                    .map(VariableDeclarator::getNameAsString)
                                    .collect(Collectors.toList());

                            result.add(new ClassInfo(packageName, className, imports, methods, fields, path, isTestClass));
                        }

                    } catch (IOException e) {
                        System.err.println("Failed to parse " + path + ": " + e.getMessage());
                    }
                });

        return result;
    }
}