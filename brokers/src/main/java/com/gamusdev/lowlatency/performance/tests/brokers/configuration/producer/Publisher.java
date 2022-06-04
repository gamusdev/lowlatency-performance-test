package com.gamusdev.lowlatency.performance.tests.brokers.configuration.producer;

import com.gamusdev.lowlatency.performance.tests.brokers.configuration.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Publisher class
 * Send the integers to the broker
 * Loaded only if the broker.clientType environment variable is configured to PUB
 */
@Configuration
@Slf4j
@ConditionalOnProperty(
        value="broker.clientType",
        havingValue = "PUB",
        matchIfMissing = false)
public class Publisher {

    /**
     * Configuration read from environment variables
     */
    @Autowired
    private final Config config;

    /**
     * Constructor
     * @param config configuration
     */
    public Publisher(Config config) {
        this.config = config;
    }

    /**
     * Supplier to send the integers and the final signal
     * @return The Supplier
     */
    @Bean
    public Supplier<Flux<Integer>> onIntegersMeasured() {

        log.info("****** Start Broker Test: Publishing data. Sending {} integers, checksum {} ******",
                config.getSizeTest(), getChecksum());

        // Create the Flux<Integer> supplier, adding the CLOSE_ID signal to finish the test
        return () -> Flux.fromStream(
                IntStream.rangeClosed(1, config.getSizeTest()).boxed()
        ).concatWith(Mono.just(Config.CLOSE_ID));
    }

    /**
     * Calculate the checksum of the data that will be sent
     * @return the checksum
     */
    private long getChecksum() {
        return LongStream.rangeClosed(1, config.getSizeTest()).sum();
    }
}
