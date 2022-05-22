package com.gamusdev.lowlatency.performance.tests.brokers.configuration.producer;

import com.gamusdev.lowlatency.performance.tests.brokers.configuration.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PublisherTest {

    @Mock
    private Config config;

    @InjectMocks
    private Publisher publisher;

    @Test
    public void onIntegersMeasuredTest() {
        // When
        final int mockSizeTest = ThreadLocalRandom.current().nextInt(0, 1000);
        when(config.getSizeTest()).thenReturn( mockSizeTest );

        // Then
        final Supplier<Flux<Integer>> supplier = publisher.onIntegersMeasured();

        // Verify
        StepVerifier.create(supplier.get()).
                expectNextCount( mockSizeTest +1 ).
                thenConsumeWhile(Config.CLOSE_ID::equals).
                verifyComplete();
    }
}
