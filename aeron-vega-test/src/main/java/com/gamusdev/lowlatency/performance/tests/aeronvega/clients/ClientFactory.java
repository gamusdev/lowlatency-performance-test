package com.gamusdev.lowlatency.performance.tests.aeronvega.clients;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Factory class to return the required Executor
 * Strategy Pattern
 */
@Slf4j
public class ClientFactory implements IClientFactory{

    /**
     * Map with the relationship between TestType and test to execute.
     */
    private Map<IClient.ClientTypeEnum, IClient> clientsMap = new HashMap<>();

    /**
     * Constructor
     * Initializes clientsMap using Java 11 ServiceLoader.
     * The service loader uses META-INF/services/ files to inject the services
     * See: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html
     */
    public ClientFactory() {
        super();
        ServiceLoader.load(IClient.class).
                forEach( s -> clientsMap.put(s.getClientType(), s) );
        log.info("IClients loaded: {}", clientsMap.toString());
    }

    /**
     * Factory method that creates an instance of the desired client
     * @param testType ClientTypeEnum.PUB or ClientTypeEnum.SUB
     * @return the instance of the desired type
     */
    public IClient getInstance(final IClient.ClientTypeEnum testType){
        return clientsMap.get(testType);
    }
}
