package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.VegaInstance;
import com.bbva.kyof.vega.protocol.common.VegaInstanceParams;
import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.ClientFactory;
import com.gamusdev.lowlatency.performance.tests.aeronvega.parser.VegaCommandLineParser;
import com.gamusdev.lowlatency.performance.tests.aeronvega.parser.LaunchParameters;
import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import com.gamusdev.lowlatency.performance.tests.aeronvega.model.TestResults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;

import java.io.IOException;


/**
 * The Executor Class initializes the instances and launch the test.
 * - Creates the Vega Instance
 * - Creates and launch the publisher (PUB) or subscriber (SUB) and launch the client
 * - Close the Vega instance when finished.
 * - Finally, print the results
 */
@Slf4j
public class VegaTestExecutor implements ITestExecutor {

    /** Instance name*/
    private static final String INSTANCE_NAME = "TestInstance";

    /**
     * Execute the test with the received params
     * @param args The given parameters
     */
    public void executeTest(String[] args)
            throws ParseException, GenericAeronVegaException {

        // Create a command line parser, parse and validate the parameters
        final VegaCommandLineParser parser = new VegaCommandLineParser();
        final LaunchParameters launchParameters = parser.parseCommandLine(args);

        log.info("Launching VegaExecutor with parameters [{}]", launchParameters);

        // Create the instance parameters
        final VegaInstanceParams params = VegaInstanceParams.builder().
                instanceName(INSTANCE_NAME).
                configurationFile(launchParameters.getVegaConfigFilePath()).build();

        final TestResults testResults;
        try {
            // Create the Vega new instance
            final IVegaInstance instance = VegaInstance.createNewInstance(params);

            // Execute the test (PUB -> Publisher, SUB -> Subscriber)
            testResults = new ClientFactory()
                    .getInstance(launchParameters.getClientTypeEnum())
                    .run(instance, launchParameters.getSizeTest());

            // Once finnished, close the Vega instance
            instance.close();

            // Print the results
            printResults( log, testResults );

        } catch (VegaException | IOException | InterruptedException e) {
            log.error("Error ", e);
        }

    }


}
