package com.gamusdev.lowlatency.performance.tests.brokers.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Config class
 */
@Configuration
@Getter
public class Config {

    /** Signal to finish the performance test */
    public static final Integer CLOSE_ID = Integer.MAX_VALUE;

    /** The number of messages sent / received in this performance test */
    @Value("${broker.sizeTest:1000000}")
    private int sizeTest;

}
