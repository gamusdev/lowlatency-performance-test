package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.VegaInstance;
import com.bbva.kyof.vega.protocol.common.VegaInstanceParams;
import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.ClientFactory;
import com.gamusdev.lowlatency.performance.tests.aeronvega.parser.ICommandLineParser;
import com.gamusdev.lowlatency.performance.tests.aeronvega.parser.VegaCommandLineParser;
import com.gamusdev.lowlatency.performance.tests.aeronvega.parser.LaunchParameters;
import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import com.gamusdev.lowlatency.performance.tests.aeronvega.model.TestResults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.Optional;
import java.util.ServiceLoader;


/**
 * The Executor Class initializes the instances and launch the test.
 * - Creates the Vega Instance
 * - Creates and launch the publisher (PUB) or subscriber (SUB) and launch the client
 * - Close the Vega instance when finished.
 * - Finally, print the results
 */
@Slf4j
public class VegaPerformanceTestExecutor implements ITestExecutor {

    /** Instance name*/
    private static final String INSTANCE_NAME = "TestInstance";

    /**
     * Execute the test with the received params
     * @param args The given parameters
     */
    public void executeTest(String[] args)
            throws ParseException, GenericAeronVegaException {

        // Create a command line parser, parse and validate the parameters
        final Optional<ICommandLineParser> optionalICommandLineParser = ServiceLoader.load(ICommandLineParser.class).findFirst();
        if (optionalICommandLineParser.isEmpty()) {
            log.error("ICommandLineParser NOT FOUND");
            System.exit(1);
        }
        final ICommandLineParser parser = optionalICommandLineParser.get();

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
            testResults = ClientFactory
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
