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
public class ClientFactory {

    /**
     * Map with the relationship between TestType and test to execute.
     */
    private static Map<IClient.ClientTypeEnum, IClient> CLIENTS = new HashMap<>();

    /**
     * Initializes clientsMap using Java 11 ServiceLoader.
     * The service loader uses META-INF/services/ files to inject the services
     * See: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html
     */
    static {
        ServiceLoader.load(IClient.class).
                forEach( s -> CLIENTS.put(s.getClientType(), s) );
        log.info("IClients loaded: {}", CLIENTS.toString());
    }

    /**
     * Factory method that creates an instance of the desired client
     * @param testType ClientTypeEnum.PUB or ClientTypeEnum.SUB
     * @return the instance of the desired type
     */
    public static IClient getInstance(final IClient.ClientTypeEnum testType){
        return CLIENTS.get(testType);
    }
}
