package com.gamusdev.lowlatency.performance.tests.aeronvega.utils;

import com.bbva.kyof.vega.msg.PublishResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BackPressureManagerTest {

    private static final int MAX_SLEEP = 1000;
    private static final int LAST_BACK_PRESSURED = 10;

    @Test
    public void checkAndControlWithoutBackPresure() {
        Assertions.assertFalse(BackPressureManager.checkAndControl(PublishResult.OK));
    }


    @Test
    public void checkAndControlWithBackPresure() throws InterruptedException {
        Assertions.assertTrue(BackPressureManager.checkAndControl(PublishResult.BACK_PRESSURED));

        Thread.sleep(LAST_BACK_PRESSURED);

        long start = System.currentTimeMillis();
        Assertions.assertTrue(BackPressureManager.checkAndControl(PublishResult.BACK_PRESSURED));

        long finish = System.currentTimeMillis();

        // Test that the check sleeps less than MAX_SLEEP / 10 second
        Assertions.assertTrue( finish - start < MAX_SLEEP / LAST_BACK_PRESSURED);
    }
}
