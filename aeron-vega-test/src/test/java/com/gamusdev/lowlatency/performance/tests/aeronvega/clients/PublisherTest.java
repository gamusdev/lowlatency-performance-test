package com.gamusdev.lowlatency.performance.tests.aeronvega.clients;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.msg.PublishResult;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.publisher.ITopicPublisher;
import com.gamusdev.lowlatency.performance.tests.aeronvega.model.TestResults;
import com.gamusdev.lowlatency.performance.tests.aeronvega.utils.Checksum;
import com.gamusdev.lowlatency.performance.tests.aeronvega.utils.Constants;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class PublisherTest {

    private static final int SIZE_TEST = 1_000;

    private static final int MAX_BACKPRESSURE_OCCURRENCES = 20;

    private static final int PAYLOAD_SIZE = 4;

    @Mock
    private IVegaInstance instance;

    @InjectMocks
    private Publisher publisher;

        @Test
    public void runOkTest () throws VegaException, InterruptedException {
        // When

        // Prepare the topicPublisher to always return OK
        final ITopicPublisher topicPublisher = Mockito.mock(ITopicPublisher.class);
        Mockito.when(topicPublisher.sendMsg(any(UnsafeBuffer.class), eq(0), eq(PAYLOAD_SIZE))).thenReturn(PublishResult.OK);

        Mockito.when(instance.createPublisher(Constants.TOPIC_NAME)).thenReturn(topicPublisher);

        // Then
        final TestResults result = publisher.run(instance, SIZE_TEST);

        // Verify
        Assertions.assertEquals(SIZE_TEST, result.getTotalMessages());
        Assertions.assertTrue(result.getDuration() > 0);
        Assertions.assertEquals(Checksum.getChecksum(SIZE_TEST), result.getChecksum());

        // Verify that the number of messages sent is the SIZE_TEST + 1 (the end signal)
        Mockito.verify(topicPublisher, Mockito.times(SIZE_TEST+1)).sendMsg(any(UnsafeBuffer.class), eq(0), eq(PAYLOAD_SIZE));
    }

    @Test
    public void getClientTypeTest() {
        Assertions.assertEquals(IClient.ClientTypeEnum.PUB, publisher.getClientType());
    }

    @Test
    public void runWithBackPressureOkTest () throws VegaException, InterruptedException {
        // When

        // Prepare the topicPublisher
        final ITopicPublisher topicPublisher = Mockito.mock(ITopicPublisher.class);
        Mockito.when(topicPublisher.sendMsg(any(UnsafeBuffer.class), eq(0), eq(PAYLOAD_SIZE))).thenReturn(PublishResult.OK);

        // Prepare the topicPublisher Mock to return 3 BackPressure responses:
        final int backPressureOccurrences = ThreadLocalRandom.current().nextInt(0, MAX_BACKPRESSURE_OCCURRENCES);
        IntStream.rangeClosed( 1, backPressureOccurrences )
                .forEach( id -> {
                    // 1) Create the buffers
                    final UnsafeBuffer sendBuffer = new UnsafeBuffer(ByteBuffer.allocate(PAYLOAD_SIZE));
                    // 2) Prepare the IDs that will produce the BackPressure
                    sendBuffer.putInt(0, id);
                    // 3) Configure the topicPublisher Mock to return BackPressure with this IDs
                    Mockito.when(topicPublisher.sendMsg(eq(sendBuffer), eq(0), eq(PAYLOAD_SIZE)))
                            .thenReturn(PublishResult.BACK_PRESSURED)
                            .thenReturn(PublishResult.OK);
                });

        Mockito.when(instance.createPublisher(Constants.TOPIC_NAME)).thenReturn(topicPublisher);

        // Then
        final TestResults result = publisher.run(instance, SIZE_TEST);

        // Verify
        Assertions.assertEquals(SIZE_TEST, result.getTotalMessages());
        Assertions.assertTrue(result.getDuration() > 0);
        Assertions.assertEquals(Checksum.getChecksum(SIZE_TEST), result.getChecksum());

        // Verify that the number of messages sent is the SIZE_TEST + 1 (the end signal) + backPressureOccurrences (resends by BackPressure)
        Mockito.verify(topicPublisher, Mockito.times(SIZE_TEST + 1 + backPressureOccurrences))
                .sendMsg(any(UnsafeBuffer.class), eq(0), eq(PAYLOAD_SIZE));
    }
}
