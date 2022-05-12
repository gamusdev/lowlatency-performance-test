package com.gamusdev.lowlatency.performance.tests.aeronvega;

import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import com.gamusdev.lowlatency.performance.tests.aeronvega.executor.VegaTestExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;


/**
 * Main class of the aeron-Vega-test
 */
@Slf4j
public class Launcher {

    public static void main(String[] args)
            throws ParseException, GenericAeronVegaException {

        // Execute the desired tests
        new VegaTestExecutor().executeTest(args);

    }
}
