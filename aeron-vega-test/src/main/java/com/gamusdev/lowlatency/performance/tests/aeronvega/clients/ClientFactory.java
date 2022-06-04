package com.gamusdev.lowlatency.performance.tests.aeronvega.clients;

import lombok.extern.slf4j.Slf4j;

import java.util.ServiceLoader;

/**
 * Factory class to return the required Executor
 * Strategy Pattern
 */
@Slf4j
public final class ClientFactory {

    /**
     * private Constructor
     */
    private ClientFactory() {
    }

    /**
     * Factory method that returns an instance of the desired client.
     * Lazy initialization: Only load the objects as they are requested
     * The service loader uses META-INF/services/ files to inject the services
     * See: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html
     * @param testType ClientTypeEnum.PUB or ClientTypeEnum.SUB
     * @return the instance of the desired type
     */
    public static IClient getInstance(final IClient.ClientTypeEnum testType){
        return ServiceLoader.load(IClient.class).stream()
                .map(ServiceLoader.Provider::get)
                .filter( c -> c.getClientType().equals(testType))
                .findFirst().get();
    }
}
