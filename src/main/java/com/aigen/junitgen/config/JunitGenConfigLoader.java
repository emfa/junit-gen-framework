package com.aigen.junitgen.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JunitGenConfigLoader {

    public static JunitGenConfig loadConfig(File projectRoot) {
        File configFile = new File(projectRoot, ".junitgenrc");

        JunitGenConfig config;
        if (configFile.exists()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(configFile, JunitGenConfig.class);
            } catch (IOException e) {
                System.err.println("Failed to read .junitgenrc, using defaults.");
                config = new JunitGenConfig();
            }
        } else {
            System.out.println("No .junitgenrc found in target project. Using default config.");
            config = new JunitGenConfig();
        }

        // 🔍 Print the active config
        System.out.println("Active Config:");
        System.out.println("  model: " + config.model);
        System.out.println("  dryRun: " + config.dryRun);
        System.out.println("  defaultOutputDir: " + config.defaultOutputDir);

        return config; // defaults
    }
}