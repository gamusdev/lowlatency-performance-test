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

    @Test
    public void runOk() throws VegaException, InterruptedException, BrokenBarrierException {
        // **** Initialize
        // Launch the Subscriber
        final Runnable subcriberRunnable = () -> launchSubscriber(instance, SIZE_TEST);
        new Thread(subcriberRunnable).start();

        // Wait the Subscriber some time to get up
        Thread.sleep(500);

        // **** When
        // Capture the Subscriber's listener
        final ArgumentCaptor<ITopicSubListener> listenerCaptor = ArgumentCaptor.forClass(ITopicSubListener.class);
        Mockito.verify( instance ).subscribeToTopic(eq(Constants.TOPIC_NAME), listenerCaptor.capture());
        final ITopicSubListener listener = listenerCaptor.getValue();
        //var capture = listenerCaptor.getAllValues();

        // Prepare the data to send to the Subscriber
        final IRcvMessage msg = Mockito.mock(IRcvMessage.class);
        Mockito.when(msg.getContentOffset()).thenReturn(0);
        final UnsafeBuffer receiverBuffer = new UnsafeBuffer(ByteBuffer.allocate(PAYLOAD_SIZE));

        // **** Then
        // Send WARM_UP data
        /*IntStream.rangeClosed( 1, SIZE_TEST/10 )
                .forEach( id -> {
                    receiverBuffer.putInt(0, id);
                    Mockito.when(msg.getContents()).thenReturn(receiverBuffer);
                    listener.onMessageReceived(msg);
                });*/

        // Send SIZE_TEST data
        IntStream.rangeClosed( 1, SIZE_TEST )
                .forEach( id -> {
                    receiverBuffer.putInt(0, id);
                    Mockito.when(msg.getContents()).thenReturn(receiverBuffer);
                    listener.onMessageReceived(msg);
                });

        // Send end Signal
        receiverBuffer.putInt(0, CLOSE_ID);
        Mockito.when(msg.getContents()).thenReturn(receiverBuffer);
        listener.onMessageReceived(msg);

        // Wait until the test is finished
        cyclicBarrier.await();

        // Verify
        Assertions.assertEquals(SIZE_TEST, result.getTotalMessages());
        Assertions.assertTrue(result.getDuration() > 0);
        Assertions.assertEquals(Checksum.getChecksum(SIZE_TEST), result.getChecksum());
    }

    @Test
    public void getClientTypeTest() {
        Assertions.assertEquals(IClient.ClientTypeEnum.SUB, subscriber.getClientType());
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
}
