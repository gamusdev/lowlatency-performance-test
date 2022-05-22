package com.gamusdev.lowlatency.performance.tests.aeronvega;

import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import com.gamusdev.lowlatency.performance.tests.aeronvega.executor.ITestExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;

import java.util.Optional;
import java.util.ServiceLoader;


/**
 * Main class of the aeron-Vega-test
 */
@Slf4j
public class Launcher {

    public static void main(String[] args)
            throws ParseException, GenericAeronVegaException {


        /**
         * Initialize the TestExecutor using Java 11 ServiceLoader.
         * The service loader uses META-INF/services/ files to inject the services
         * See: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html
         */
        Optional<ITestExecutor> optTestExecutor = ServiceLoader.load(ITestExecutor.class).findFirst();

        if (optTestExecutor.isPresent()) {
            // Execute the desired tests
            optTestExecutor.get().executeTest(args);
        } else {
            log.error("ITestExecutor NOT FOUND");
        }

    }
}
