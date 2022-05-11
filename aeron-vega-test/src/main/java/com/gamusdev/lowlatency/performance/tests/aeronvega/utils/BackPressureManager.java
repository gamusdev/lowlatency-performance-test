package com.gamusdev.lowlatency.performance.tests.aeronvega.utils;

import com.bbva.kyof.vega.msg.PublishResult;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Control flow for managing backpressure adverts
 */
@Slf4j
public class BackPressureManager {

    /** Last back pressured timestamp*/
    private static AtomicLong lastBackPressured = new AtomicLong(0);

    /**
     * Checks if the result msg is indicates back pressured and sleep some time to control it.
     *
     * This is an easy way to manage flow control:
     * - It takes the maximum wait a time as a second.
     * - It takes the time between the last two back pressured situations (fromLastBackPresured). The
     * smaller this time is, the more congested the system is.
     * - So, the time to sleep is 1 sec / fromLastBackPresured
     * - Note: To avoid a division by 0,
     * fromLastBackPresured =  System.currentTimeMillis() - lastBackPressured.get() + 1
     *
     * @param result results after a publication
     * @return boolean indicating if the result is back pressured or not
     */
    public static boolean checkAndControl(PublishResult result) {

        // Check if the result is back pressured
        if (result == PublishResult.BACK_PRESSURED) {
            try {

                // Calculates the time to sleep
                long fromLastBackPresure = System.currentTimeMillis() - lastBackPressured.get() + 1;
                long timeToSleep = 1000 / fromLastBackPresure;

                log.info("{} detected. Waiting {} ms...", result, timeToSleep);

                // Sleep to give time to the subscriber to process the data
                Thread.sleep(timeToSleep);

                // Update the new timestamp
                lastBackPressured.set( System.currentTimeMillis() );

                return true;
            } catch (InterruptedException e) {
                log.error("Unexpected ERROR. Finishing the test", e);
                System.exit(1);
            }
        }
        return false;

    }
}
