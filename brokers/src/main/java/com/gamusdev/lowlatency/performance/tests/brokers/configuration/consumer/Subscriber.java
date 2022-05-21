package com.gamusdev.lowlatency.performance.tests.brokers.configuration.consumer;

import com.gamusdev.lowlatency.performance.tests.brokers.configuration.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Configuration
@Slf4j
@ConditionalOnProperty(
        value="broker.clientType",
        havingValue = "SUB",
        matchIfMissing = false)
public class Subscriber {

    @Autowired
    private final Config config;

    /** Messages received */
    private final AtomicInteger receivedCounter = new AtomicInteger(0);

    /** The checksum is the sum of all the messageId published */
    private final AtomicLong checksum = new AtomicLong(0);

    /** Start time, when te first message is received */
    long startTime = 0;

    /** Constructor */
    public Subscriber(Config config) {
        this.config = config;
    }

    /**
     * Consumer to receive the integers and the final signal
     * @return The Consumer
     */
    @Bean
    public Consumer<Integer> onIntegersMeasured() {

        log.info("****** Start Broker Test: Consumer waiting for {}", config.getSizeTest());

        return data -> {
            int count = receivedCounter.getAndIncrement();

            //log.info("receivedCount {}, data {}", count, data);

            if (count == 1) {
                startTime = System.currentTimeMillis();
            }
            else if ( Config.CLOSE_ID.equals(data)) {
                long finishTime = System.currentTimeMillis() - startTime;
                log.info("****** Broker Test Finished: Duration: {}," +
                        "Received {} messages, checksum {}",
                        finishTime, count, checksum);

                // Exit the Consumer
                System.exit(0);
            }

            // Add the received data to the checksum
            checksum.addAndGet(data);
        };
    }
}
