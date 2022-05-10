package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.LaunchParameters;

@FunctionalInterface
public interface TestExecutor {
    void executeTest(LaunchParameters launchParameters);
}
