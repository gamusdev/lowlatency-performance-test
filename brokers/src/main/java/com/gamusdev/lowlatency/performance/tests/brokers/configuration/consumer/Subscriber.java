package com.gamusdev.lowlatency.performance.tests.brokers.configuration.consumer;

import com.gamusdev.lowlatency.performance.tests.brokers.configuration.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Subscriber class
 * Receives the integers from the broker
 * Loaded only if the broker.clientType environment variable is configured to SUB
 */
@Configuration
@Slf4j
@ConditionalOnProperty(
        value="broker.clientType",
        havingValue = "SUB",
        matchIfMissing = false)
public class Subscriber {

    /**
     * Configuration read from environment variables
     */
    @Autowired
    private final Config config;

    /** Messages received */
    private final AtomicInteger receivedCounter = new AtomicInteger(0);

    /** The checksum is the sum of all the messageId published */
    private final AtomicLong checksum = new AtomicLong(0);

    /** Constructor */
    public Subscriber(Config config) {
        this.config = config;
    }

    /**
     * Consumer to receive the integers and the final signal
     * @return The Consumer
     */
    @Bean
    public Consumer<Flux<Integer>> onIntegersMeasured() {

        log.info("****** Start Broker Test: Consumer waiting for {}", config.getSizeTest());

        // Update counters
        return data -> data.
                    //log().
                    filter(d-> !Config.CLOSE_ID.equals(d)).
                    subscribe( d -> {
                        receivedCounter.incrementAndGet();
                        checksum.addAndGet(d);
                    });
    }

    @PreDestroy
    public void destroy() {
        log.info("****** Broker Test Finished: Received {} messages, checksum {} ******",
                receivedCounter.get(), checksum);
    }

}
