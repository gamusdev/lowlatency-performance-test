package com.gamusdev.lowlatency.performance.tests.aeronvega.clients;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * Factory class to return the required Executor
 * Strategy Pattern
 */
@Slf4j
public final class ClientFactory {

    /**
     * Map with the relationship between TestType and test to execute.
     */
    private static Map<IClient.ClientTypeEnum, IClient> CLIENTS;

    /**
     * Initializes clientsMap using Java 11 ServiceLoader.
     * The service loader uses META-INF/services/ files to inject the services
     * See: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html
     */
    static {
        CLIENTS = ServiceLoader.load(IClient.class).stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toMap(IClient::getClientType, s -> s));
        log.info("IClients loaded: {}", CLIENTS);
    }

    /**
     * private Constructor
     */
    private ClientFactory() {
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
