package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import org.apache.commons.cli.ParseException;

@FunctionalInterface
public interface ITestExecutor {
    /**
     * Execute the test with the received params
     * @param args The given parameters
     */
    void executeTest(String[] args) throws ParseException, GenericAeronVegaException;
}
