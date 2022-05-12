package com.gamusdev.lowlatency.performance.tests.aeronvega.clients;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.gamusdev.lowlatency.performance.tests.aeronvega.model.TestResults;

/**
 * Interface to define a Client Vega Instance
 */
@FunctionalInterface
public interface IClient {

    /** Enum ClientType to indicate PUB (publisher) or SUB (subscriber) client type*/
    enum ClientTypeEnum { PUB, SUB }

    /** Signal to finish the test */
    int CLOSE_ID = Integer.MAX_VALUE;

    /**
     * When starts, the JVN creates some optimizations, but it needs some training.
     * Number of messages sent to warn up the JVM.
     * */
    int WARN_UP_MESSAGES = 1_000_000;

    /** Execute the client code
     *
     * @param instance Vega Instance
     * @param sizeTest Number of messages to send in the test
     * @throws VegaException Vega Exception
     * @throws InterruptedException Interrupted Exception
     */
    TestResults run (IVegaInstance instance, int sizeTest) throws VegaException, InterruptedException;

}
