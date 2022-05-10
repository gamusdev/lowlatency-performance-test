package com.gamusdev.lowlatency.performance.tests.aeronvega;

import com.bbva.kyof.vega.exception.VegaException;
import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.LaunchParameters;
import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.TestType;
import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import com.gamusdev.lowlatency.performance.tests.aeronvega.executor.FactoryExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;

/**
 * Main class of the aeron-Vega-test
 */
@Slf4j
public class main {

    public static void main(String[] args)
            throws ParseException, GenericAeronVegaException {

        // Create a command line parser, parse and validate the parameters
        final CommandLineParser parser = new CommandLineParser();
        final LaunchParameters launchParameters = parser.parseCommandLine(args);

        log.info("Launching executor with parameters [{}]", launchParameters);

        // Execute the desired tests
        if (TestType.ALL != launchParameters.getTestType()) {
            FactoryExecutor.getInstance(launchParameters.getTestType()).executeTest(launchParameters);
        }
        /*else {
            // Execute all the tests
            Arrays.stream(TestType.values()).filter(t -> t.equals(TestType.ALL))
                    .forEach( t ->
                            FactoryExecutor.getInstance(t).executeTest(launchParameters)
                    );
        }*/
    }
}
