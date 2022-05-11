package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.IClient;
import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.Publisher;
import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.Subscriber;
import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.ClientTypeEnum;

import java.util.Map;

/**
 * Factory class to return the required Executor
 */
public class ClientFactory {

    /** Strategy Pattern
     * Map with the relationship between TestType and test to execute.
     */
    private static Map<ClientTypeEnum, IClient> clientsMap =
            Map.ofEntries(
                    Map.entry(ClientTypeEnum.PUB, new Publisher()),
                    Map.entry(ClientTypeEnum.SUB, new Subscriber())
            );

    /** Factory method */
    public static IClient getInstance(final ClientTypeEnum testType){
        return clientsMap.get(testType);
    }
}
