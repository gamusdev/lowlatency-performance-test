package com.gamusdev.lowlatency.performance.tests.aeronvega.clients;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.gamusdev.lowlatency.performance.tests.aeronvega.model.TestResults;

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
