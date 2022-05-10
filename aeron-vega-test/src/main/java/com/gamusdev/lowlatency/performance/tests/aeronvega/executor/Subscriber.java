package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.msg.IRcvMessage;
import com.bbva.kyof.vega.msg.IRcvRequest;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.subscriber.ITopicSubListener;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Subscriber implements IClient {

    /** Name of the topic */
    private static final String TOPIC_NAME = "VegaTopic";

    // ID to finnish testing
    private static final int CLOSE_ID = Integer.MAX_VALUE;
    // Messages received
    private AtomicInteger receivedMsgs = new AtomicInteger();
    // Flag to close
    boolean close;

    public void run(final IVegaInstance instance) throws VegaException, InterruptedException {

        // Create a listener
        ITopicSubListener listener = new ITopicSubListener()
        {
            @Override
            public void onMessageReceived(IRcvMessage receivedMessage)
            {
                receivedMsgs.getAndIncrement();

                // Get the offset of the message in the buffer
                int msgOffset = receivedMessage.getContentOffset();

                // In this case, the received value will not been save, soo, it is not necessary to
                // allocate a new ByteBuffer. It is used the unsafeBuffer directly
                int receivedId = receivedMessage.getContents().getInt(msgOffset, ByteOrder.nativeOrder());

                log.info("Received messageId {}", receivedId);

                if(receivedId == CLOSE_ID)
                {
                    close=true;
                }
            }

            @Override
            public void onRequestReceived(IRcvRequest receivedRequest) {
            }
        };

        try
        {
            // Subscribe to the topic
            instance.subscribeToTopic(TOPIC_NAME, listener);

            while(!close)
            {
                Thread.sleep(1);
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Method to close the subscriber
     * @throws IOException
     */
    public void close() throws IOException {
        log.info("Closing subscriber");
        //instance.close();
        log.info("Closed tested subscriber with receivedMsgs={}", receivedMsgs);
    }
}
