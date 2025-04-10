package com.aigen.junitgen.ai;

import com.aigen.junitgen.model.AIInput;

public interface AITestGenerator {
    String generateTestClass(AIInput input);
}