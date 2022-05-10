package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.TestType;
import com.gamusdev.lowlatency.performance.tests.aeronvega.executor.vega.VegaExecutor;

import java.util.Map;

/**
 * Factory class to return the required Executor
 */
public class FactoryExecutor {

    /** Strategy Pattern
     * Map with the relationship between TestType and test to execute.
     */
    private static Map<TestType, TestExecutor> testExecutorMap =
            Map.ofEntries(
                    //Map.entry(TestType.TCP, null),
                    //Map.entry(TestType.UDP, null),
                    Map.entry(TestType.VEGA, new VegaExecutor())
                    //Map.entry(TestType.RABBITMQ, null),
                    //Map.entry(TestType.KAFKA, null)
            );

    /** Factory method */
    public static TestExecutor getInstance(final TestType testType){
        return testExecutorMap.get(testType);
    }
}
