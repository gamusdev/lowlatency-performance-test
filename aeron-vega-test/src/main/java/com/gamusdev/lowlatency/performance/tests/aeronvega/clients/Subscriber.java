package com.gamusdev.lowlatency.performance.tests.aeronvega.clients;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.msg.IRcvMessage;
import com.bbva.kyof.vega.msg.IRcvRequest;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.subscriber.ITopicSubListener;
import com.gamusdev.lowlatency.performance.tests.aeronvega.utils.Constants;
import com.gamusdev.lowlatency.performance.tests.aeronvega.model.TestResults;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Vega Subscriber
 * This class subscribe to the topic, and receives all the integers.
 */
@Slf4j
public class Subscriber implements IClient {

    /** The checksum is the sum of all the messageId published */
    private final AtomicLong checksum = new AtomicLong();

    /** Enum ClientType to indicate SUB (subscriber) */
    private final ClientTypeEnum clientType = ClientTypeEnum.SUB;

    /** Messages received */
    private final AtomicInteger receivedCounter = new AtomicInteger();

    /** Flag to close */
    private boolean closeFlag;

    /**
     * Constructor
     */
    public Subscriber() {
        super();
    }

    /**
     * Create the listener.
     * Vega executes the listener on other thread!
     * @return the listener created
     */
    private ITopicSubListener getListener() {
        return new ITopicSubListener()
        {
            @Override
            public void onMessageReceived(IRcvMessage receivedMessage)
            {

                // Get the offset of the message in the buffer
                final int msgOffset = receivedMessage.getContentOffset();

                // In this case, the received value will not been safe, soo, it is not necessary to
                // allocate a new ByteBuffer. It is used the unsafeBuffer directly
                final int receivedId = receivedMessage.getContents().getInt(msgOffset, ByteOrder.nativeOrder());

                // If close signal received, active close flag & exit
                if(receivedId == CLOSE_ID) {
                    closeFlag=true;
                    return;
                }
                else {
                    checksum.addAndGet(receivedId);
                }

                // Increment the received messages
                receivedCounter.incrementAndGet();
            }

            @Override
            public void onRequestReceived(IRcvRequest receivedRequest) {
                // Is not used in the test
            }

        };
    }

    /**
     * Execute the test
     * Wait until the all the messages are received and return the duration of the test
     * @return the duration of the test
     */
    private long executeTest() throws InterruptedException {
        // Start time
        long startTime = 0;

        // Wait until the close signal is received
        while(!closeFlag) {
            // If the first message is received, take the startTime.
            if (receivedCounter.get() > 1 && startTime == 0) {
                startTime = System.currentTimeMillis();
            }

            Thread.sleep(1);
        }

        // Take the duration
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Subscribe to the topic and receive the integers
     * @param instance Vega Instance
     * @throws VegaException Vega Exception
     * @throws InterruptedException Interrupted Exception
     */
    public TestResults run(final IVegaInstance instance, int sizeTest)
            throws VegaException, InterruptedException {

        // Create a listener
        final ITopicSubListener listener = getListener();

        // Subscribe the listener to the topic
        instance.subscribeToTopic(Constants.TOPIC_NAME, listener);

        final long durationTime = executeTest();

        if ( sizeTest != receivedCounter.get()) {
            log.info("###########################################################################################");
            log.error("ERROR: Some messages were lost! size of the test: {}, received messages: {}", sizeTest, receivedCounter.get());
            log.info("###########################################################################################");
        }

        // Return the results
        return TestResults.builder()
                .totalMessages(receivedCounter.get())
                .duration(durationTime)
                .checksum(checksum.get())
                .build();
    }

    public ClientTypeEnum getClientType() {
        return clientType;
    }
}
