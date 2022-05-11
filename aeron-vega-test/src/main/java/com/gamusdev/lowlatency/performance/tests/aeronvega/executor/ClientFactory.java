package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.IClient;
import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.Publisher;
import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.Subscriber;

import java.util.Map;

/**
 * Factory class to return the required Executor
 */
public class ClientFactory {

    /** Strategy Pattern
     * Map with the relationship between TestType and test to execute.
     */
    private Map<IClient.ClientTypeEnum, IClient> clientsMap =
            Map.ofEntries(
                    Map.entry(Publisher.CLIENT_TYPE, new Publisher()),
                    Map.entry(Subscriber.CLIENT_TYPE, new Subscriber())
            );

    /**
     * Factory method that creates an instance of the desired client
     * @param testType ClientTypeEnum.PUB or ClientTypeEnum.SUB
     * @return the instance of the desired type
     */
    public IClient getInstance(final IClient.ClientTypeEnum testType){
        return clientsMap.get(testType);
    }
}
