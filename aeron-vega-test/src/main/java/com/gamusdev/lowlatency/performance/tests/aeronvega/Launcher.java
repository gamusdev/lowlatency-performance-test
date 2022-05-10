package com.gamusdev.lowlatency.performance.tests.aeronvega;

import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.LaunchParameters;
import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import com.gamusdev.lowlatency.performance.tests.aeronvega.executor.VegaExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;


/**
 * Main class of the aeron-Vega-test
 */
@Slf4j
public class Launcher {

    public static void main(String[] args)
            throws ParseException, GenericAeronVegaException {

        // Create a command line parser, parse and validate the parameters
        final CommandLineParser parser = new CommandLineParser();
        final LaunchParameters launchParameters = parser.parseCommandLine(args);

        log.info("Launching executor with parameters [{}]", launchParameters);

        // Execute the desired tests
        VegaExecutor.executeTest(launchParameters);

    }
}
