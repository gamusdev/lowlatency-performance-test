package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.ClientType;

import java.util.Map;

/**
 * Factory class to return the required Executor
 */
public class FactoryClient {

    /** Strategy Pattern
     * Map with the relationship between TestType and test to execute.
     */
    private static Map<ClientType, IClient> testExecutorMap =
            Map.ofEntries(
                    Map.entry(ClientType.PUB, new Publisher()),
                    Map.entry(ClientType.SUB, new Subscriber())
            );

    /** Factory method */
    public static IClient getInstance(final ClientType testType){
        return testExecutorMap.get(testType);
    }
}
