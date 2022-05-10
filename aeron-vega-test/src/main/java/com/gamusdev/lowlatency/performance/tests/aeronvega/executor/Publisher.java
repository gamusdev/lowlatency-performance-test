package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.msg.PublishResult;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.publisher.ITopicPublisher;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.UnsafeBuffer;

/**
 * Vega Publisher
 */
@Slf4j
public class Publisher implements IClient {

    /** Number of the topic */
    private static final String TOPIC_NAME = "VegaTopic";

    /**Time to wait to set up channels */
    private static int TIME_TO_SET_UP_CHANNELS = 5000;

    /** The reused buffer */
    private UnsafeBuffer sendBuffer;


    //Numero de mensajes enviados en total
    private static int TOTAL_MESSAGES_SENT = 100;
    // ID to finnish testing
    private static final int CLOSE_ID = Integer.MAX_VALUE;
    // Integer to create an unique ID for each message
    private int messageId = 0;
    // Messages lost
    private int errorMsgs = 0;
    // Messages sent
    private int sentMsgs = 0;


    public void run (final IVegaInstance instance) throws VegaException, InterruptedException {
        // Initialize the buffer
        sendBuffer = new UnsafeBuffer(ByteBuffer.allocate(128));

        // Subscribe to topics
        ITopicPublisher topicPublisher = instance.createPublisher(TOPIC_NAME);

        log.info("Waiting for channels set up");
        Thread.sleep(TIME_TO_SET_UP_CHANNELS);

        log.info("Start Vega Test ******************************************");

        for(int i = 0; i < TOTAL_MESSAGES_SENT; i++)
        {
            sendMsg(topicPublisher, false);
        }

        log.info("Stopping Publisher ******************************************");

        sendMsg(topicPublisher, true);

        close();
    }

    /**
     * Method to send a message and save it into the messages structure
     * @param topicPublisher
     */
    private void sendMsg(ITopicPublisher topicPublisher, boolean close) {

        // Create the message to send
        if(close)
        {
            messageId = CLOSE_ID;
        }
        sendBuffer.putInt(0, messageId);
        messageId++;
        PublishResult result = topicPublisher.sendMsg(sendBuffer, 0, 4);

        if (result == PublishResult.BACK_PRESSURED || result == PublishResult.UNEXPECTED_ERROR) {
            errorMsgs++;
        }
        else{
            sentMsgs++;
        }

        log.info("Sent messageId {}", messageId);
    }

    /**
     * Method to close the publisher
     * @throws IOException IOException
     */
    public void close() {
        log.info("Closing publisher");
        //instance.close();
        log.info("Closed tested publisher with sendedMsgs={} and errorMsgs={}",sentMsgs, errorMsgs);
    }
}
