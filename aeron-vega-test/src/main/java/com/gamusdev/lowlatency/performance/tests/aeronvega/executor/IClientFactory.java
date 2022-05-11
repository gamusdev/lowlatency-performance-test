package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.IClient;

/**
 * Interface to ClientFactory
 */
@FunctionalInterface
public interface IClientFactory {

    /**
     * Factory method that creates an instance of the desired client
     * @param testType ClientTypeEnum.PUB or ClientTypeEnum.SUB
     * @return the instance of the desired type
     */
    IClient getInstance(final IClient.ClientTypeEnum testType);

}
