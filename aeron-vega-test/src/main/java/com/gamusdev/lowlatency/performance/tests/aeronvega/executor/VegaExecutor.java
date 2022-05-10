package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.VegaInstance;
import com.bbva.kyof.vega.protocol.common.VegaInstanceParams;
import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.LaunchParameters;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


/**
 * The Executor Class initializes the instances and launch the test.
 * 1) Creates the Vega Instance
 * 2) Initialices the data
 * 3) Creates and launch the publisher (PUB) or subscriber (SUB)
 */
@Slf4j
public class VegaExecutor {

    private static final String INSTANCE_NAME = "TestInstance";

    public static void executeTest(LaunchParameters launchParameters) {

        log.info("Launching VegaExecutor with parameters [{}]", launchParameters);

        // Create the instance parameters
        final VegaInstanceParams params = VegaInstanceParams.builder().
                instanceName(INSTANCE_NAME).
                configurationFile(launchParameters.getVegaConfigFilePath()).build();

        try {
            // Create the Vega new instance
            final IVegaInstance instance = VegaInstance.createNewInstance(params);

            // Execute the test (PUB -> Publisher, SUB -> Subscriber)
            FactoryClient.getInstance(launchParameters.getClientType())
                    .run(instance);

            // Once finnished, close the Vega instance
            instance.close();

        } catch (VegaException | IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }

}
