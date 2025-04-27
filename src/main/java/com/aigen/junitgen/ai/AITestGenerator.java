package com.aigen.junitgen.ai;

import com.aigen.junitgen.model.AIInput;
import com.aigen.junitgen.scan.ClassIndex;

public interface AITestGenerator {
    String generateTestClass(AIInput input, boolean debugPrompt, ClassIndex classIndex);
}