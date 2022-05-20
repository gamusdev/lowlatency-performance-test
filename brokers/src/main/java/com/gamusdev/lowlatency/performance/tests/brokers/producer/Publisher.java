package com.gamusdev.lowlatency.performance.tests.brokers.producer;

import com.gamusdev.lowlatency.performance.tests.brokers.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Configuration
public class Publisher {

    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);

    /** Messages sent */
    private final AtomicInteger counter = new AtomicInteger();

    @Bean
    @DependsOn("onIntegersMeasuredConsumer")
    public Supplier<Flux<Integer>> onIntegersMeasuredSupplier() {

        logger.info("------------> Creating producer");

        return () -> Flux.fromStream(
                Stream.iterate(-1000,                   // start
                n -> n <= Constants.END,// Predicate to finish
                n -> n + 1  // Increment
            )
        );
    }
}
