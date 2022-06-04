package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import com.gamusdev.lowlatency.performance.tests.aeronvega.model.TestResults;
import org.apache.commons.cli.ParseException;

/**
 * Interface with common methods for TestExecutor.
 * Template Pattern
 */
@FunctionalInterface
public interface ITestExecutor {
    /**
     * Execute the test with the received params
     * @param args The given parameters
     */
    void executeTest(String[] args) throws ParseException, GenericAeronVegaException;

    /**
     * Print the results
     * @param testResults the results to print
     */
    default void printResults( org.slf4j.Logger log, TestResults testResults) {
        log.info("******************************************************************************************************");
        log.info(testResults.toString());
        log.info("******************************************************************************************************");
    }
}
