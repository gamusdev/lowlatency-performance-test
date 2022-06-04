package com.gamusdev.lowlatency.performance.tests.brokers.configuration.consumer;

import com.gamusdev.lowlatency.performance.tests.brokers.configuration.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@ExtendWith(SpringExtension.class)
public class SubscriberTest {

    @Mock
    private Config config;

    @InjectMocks
    private Subscriber subscriber;

    @Test
    public void onIntegersMeasuredTest() {
        // When
        final Integer[] intArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        final Consumer<Flux<Integer>> consumer = subscriber.onIntegersMeasured();
        final Flux<Integer> integers = Flux.fromArray(intArray);

        // Then
        consumer.accept( integers );

        // Verify
        // Receive count
        final AtomicInteger receivedCounter = (AtomicInteger)ReflectionTestUtils.getField(subscriber, "receivedCounter");
        Assertions.assertNotNull(receivedCounter);
        Assertions.assertEquals(intArray.length, receivedCounter.get() );

        // checksum
        final AtomicLong checksum = (AtomicLong)ReflectionTestUtils.getField(subscriber, "checksum");
        Assertions.assertNotNull(checksum);
        Assertions.assertEquals(
                Arrays.stream(intArray).mapToInt(Integer::intValue).sum(),
                checksum.get() );

        subscriber.destroy();
    }
}
