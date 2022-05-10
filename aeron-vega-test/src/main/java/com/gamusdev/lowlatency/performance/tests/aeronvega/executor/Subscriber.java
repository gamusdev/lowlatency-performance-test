package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.msg.IRcvMessage;
import com.bbva.kyof.vega.msg.IRcvRequest;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.subscriber.ITopicSubListener;
import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.Constants;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Vega Subscriber
 * This class subscribe to the topic, and receives all the integers.
 */
@Slf4j
public class Subscriber implements IClient {

    /** The checksum is the sum of all the messageId published */
    private AtomicLong checksum = new AtomicLong();

    /** Messages received */
    private AtomicInteger receivedMsgs = new AtomicInteger();

    /** Flag to close */
    boolean close;

    /**
     * Subscribe to the topic and receive the integers
     * @param instance Vega Instance
     * @throws VegaException VegaException
     */
    public void run(final IVegaInstance instance) throws VegaException {

        // Create a listener
        ITopicSubListener listener = new ITopicSubListener()
        {
            @Override
            public void onMessageReceived(IRcvMessage receivedMessage)
            {
                receivedMsgs.getAndIncrement();

                // Get the offset of the message in the buffer
                final int msgOffset = receivedMessage.getContentOffset();

                // In this case, the received value will not been save, soo, it is not necessary to
                // allocate a new ByteBuffer. It is used the unsafeBuffer directly
                final int receivedId = receivedMessage.getContents().getInt(msgOffset, ByteOrder.nativeOrder());

                if(receivedId == Constants.CLOSE_ID) {
                    close=true;
                }
                else {
                    checksum.addAndGet(receivedId);
                }
            }

            @Override
            public void onRequestReceived(IRcvRequest receivedRequest) {
                // Do not used in the test
            }
        };

        try
        {
            // Subscribe to the topic
            instance.subscribeToTopic(Constants.TOPIC_NAME, listener);

            // Start time
            long startTime = 0;

            while(!close) {
                // If the first message is received, take the startTime
                if (receivedMsgs.get() > 1 && startTime == 0) {
                    startTime = System.currentTimeMillis();
                }

                Thread.sleep(1);
            }

            // Take the duration
            final long durationTime = System.currentTimeMillis() - startTime;

            log.info("****** Finnished publisher test with receivedMsgs={}." +
                    " Checksum={} ******",receivedMsgs, checksum);

            log.info("****** Duration endTime={}ms ******\n\n", durationTime);

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
