package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.protocol.IVegaInstance;

/**
 * Interface to define a Client Vega Instance
 */
@FunctionalInterface
public interface IClient {

    /** Execute the client code
     *
     * @param instance Vega Instance
     * @param sizeTest Number of messages to send in the test
     * @throws VegaException Vega Exception
     * @throws InterruptedException Interrupted Exception
     */
    TestResults run (IVegaInstance instance, int sizeTest) throws VegaException, InterruptedException;
}
