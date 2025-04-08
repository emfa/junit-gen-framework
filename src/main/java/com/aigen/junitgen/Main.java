package com.aigen.junitgen;


import com.aigen.junitgen.cli.JUnitGenCLI;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new JUnitGenCLI()).execute(args);
        System.exit(exitCode);
    }
}
