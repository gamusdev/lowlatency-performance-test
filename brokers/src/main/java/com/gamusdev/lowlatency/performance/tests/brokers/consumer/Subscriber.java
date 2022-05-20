package com.gamusdev.lowlatency.performance.tests.brokers.consumer;

import com.gamusdev.lowlatency.performance.tests.brokers.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Configuration
public class Subscriber {
    private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);

    /** Messages received */
    private final AtomicInteger receivedCounter = new AtomicInteger(0);

    long startTime = 0;

    @Bean
    public Consumer<Integer> onIntegersMeasuredConsumer() {

        logger.info("------------> Creating consumer");

        return data -> {
            int count = receivedCounter.incrementAndGet();

            //logger.info("receivedId {}, data {}", count, data);

            if (count == 1) {
                logger.info("starting {}", data);
                startTime = System.currentTimeMillis();
            }
            else if (count == Constants.END) {
                logger.info(data.toString());
                logger.info("Duration: {}", System.currentTimeMillis() - startTime);
            }
            /*else if (count > Constants.END) {
                logger.info("Receiving OUT OF SCOPE {}", data);
            }
            else if (data == Constants.END) {
                logger.info("Data loss {}", data);
            }*/
            //logger.info(data.toString());
        };
    }
}
