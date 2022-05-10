package com.gamusdev.lowlatency.performance.tests.aeronvega.executor.vega;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.VegaInstance;
import com.bbva.kyof.vega.protocol.common.VegaInstanceParams;
import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.ClientType;
import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.LaunchParameters;
import com.gamusdev.lowlatency.performance.tests.aeronvega.executor.TestExecutor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


/**
 * The Executor Class initializes the instances and launch the test.
 * 1) Creates the Vega Instance
 * 2) Initialices the data
 * 3) Creates and launch the publisher (PUB) or subscriber (SUB)
 */
@Slf4j
public class VegaExecutor implements TestExecutor {

    @Override
    public void executeTest(LaunchParameters launchParameters) {

        log.info("Launching VegaExecutor with parameters [{}]", launchParameters);

        // Create the instance parameters
        final VegaInstanceParams params = VegaInstanceParams.builder().
                instanceName("TestInstance").
                configurationFile(launchParameters.getVegaConfigFilePath()).build();

        // Create a new instance
        try {
            final IVegaInstance instance = VegaInstance.createNewInstance(params);

            if (ClientType.PUB == launchParameters.getClientType()) {
                new Publisher().run(instance);
            }
            else {
                new Subscriber().run(instance);
            }
            instance.close();
        } catch (VegaException | IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }

}
