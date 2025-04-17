package com.aigen.junitgen.config;

import lombok.Data;

@Data
public class JunitGenConfig {
    public String model = "gemini";
    public boolean dryRun = false;
    public String defaultOutputDir = "src/test/java";
}