package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.protocol.IVegaInstance;

/**
 * Interface to define a Client Vega Instance
 */
@FunctionalInterface
public interface IClient {

    /** Execute the client code*/
    void run (IVegaInstance instance) throws VegaException, InterruptedException;
}
