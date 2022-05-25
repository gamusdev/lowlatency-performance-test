package com.gamusdev.lowlatency.performance.tests.aeronvega.clients;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.msg.IRcvMessage;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.subscriber.ITopicSubListener;
import com.gamusdev.lowlatency.performance.tests.aeronvega.model.TestResults;
import com.gamusdev.lowlatency.performance.tests.aeronvega.utils.Checksum;
import com.gamusdev.lowlatency.performance.tests.aeronvega.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.IntStream;

import static com.gamusdev.lowlatency.performance.tests.aeronvega.clients.IClient.CLOSE_ID;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class SubscriberTest {

    private static final int SIZE_TEST = 1_000;

    private static final int PAYLOAD_SIZE = 4;

    @Mock
    private IVegaInstance instance;

    private CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

    private TestResults result;

    @InjectMocks
    private Subscriber subscriber;

    @BeforeEach
    public void initEach() {
        // Launch the Subscriber
        final Runnable subcriberRunnable = () -> launchSubscriber(instance, SIZE_TEST);
        new Thread(subcriberRunnable).start();
    }

    /**
     * Launch the subscriber in another Thread
     * @param pInstance Vega Instance
     * @param pSize Size of the test
     */
    private void launchSubscriber (IVegaInstance pInstance, int pSize) {
        try {
            result = subscriber.run(pInstance, pSize);
            log.info("Result {}", result);

            // Wait until the test is finished
            cyclicBarrier.await();

        } catch (VegaException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private ITopicSubListener captureListener() throws VegaException {
        final ArgumentCaptor<ITopicSubListener> listenerCaptor = ArgumentCaptor.forClass(ITopicSubListener.class);
        Mockito.verify( instance ).subscribeToTopic(eq(Constants.TOPIC_NAME), listenerCaptor.capture());
        return listenerCaptor.getValue();
    }

    /**
     * Simulate the reception of the message.
     * @param receiverBuffer the buffer to use
     * @param iRcvMessage iRcvMessage
     * @param listener the listener
     * @param message the message to receive
     */
    private void simulateSentMessage
            ( final UnsafeBuffer receiverBuffer, final IRcvMessage iRcvMessage,
              final ITopicSubListener listener, final int message ) {
        receiverBuffer.putInt(0, message);
        Mockito.when(iRcvMessage.getContents()).thenReturn(receiverBuffer);
        listener.onMessageReceived(iRcvMessage);
    }

    @Test
    public void runOk() throws VegaException, InterruptedException, BrokenBarrierException {
        // Wait the Subscriber some time to get up
        Thread.sleep(500);

        // **** When
        // Capture the Subscriber's listener
        final ITopicSubListener listener = captureListener();

        // Prepare the data that will be received by the Subscriber
        final IRcvMessage iRcvMessage = Mockito.mock(IRcvMessage.class);
        Mockito.when(iRcvMessage.getContentOffset()).thenReturn(0);
        final UnsafeBuffer receiverBuffer = new UnsafeBuffer(ByteBuffer.allocate(PAYLOAD_SIZE));

        // **** Then
        // Send SIZE_TEST data
        IntStream.rangeClosed( 1, SIZE_TEST )
                .forEach( id -> simulateSentMessage( receiverBuffer, iRcvMessage, listener, id ) );

        // Send End Signal
        simulateSentMessage( receiverBuffer, iRcvMessage, listener, CLOSE_ID );

        // Wait until the test is finished
        cyclicBarrier.await();

        // Verify
        Assertions.assertEquals(SIZE_TEST, result.getTotalMessages());
        Assertions.assertTrue(result.getDuration() > 0);
        Assertions.assertEquals(Checksum.getChecksum(SIZE_TEST), result.getChecksum());
    }



    @Test
    public void runKO() throws VegaException, InterruptedException, BrokenBarrierException {
        // Wait the Subscriber some time to get up
        Thread.sleep(500);

        // **** When
        // Capture the Subscriber's listener
        final ITopicSubListener listener = captureListener();

        // Prepare the data to send to the Subscriber
        final IRcvMessage iRcvMessage = Mockito.mock(IRcvMessage.class);
        Mockito.when(iRcvMessage.getContentOffset()).thenReturn(0);
        final UnsafeBuffer receiverBuffer = new UnsafeBuffer(ByteBuffer.allocate(PAYLOAD_SIZE));

        // **** Then
        // Send end Signal without data
        simulateSentMessage( receiverBuffer, iRcvMessage, listener, CLOSE_ID );

        // Wait until the test is finished
        cyclicBarrier.await();

        // Verify
        Assertions.assertNotEquals(SIZE_TEST, result.getTotalMessages());
        Assertions.assertTrue(result.getDuration() > 0);
    }

    @Test
    public void getClientTypeTest() {
        Assertions.assertEquals(IClient.ClientTypeEnum.SUB, subscriber.getClientType());
    }

}
