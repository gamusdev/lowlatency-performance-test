package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import java.nio.ByteBuffer;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.msg.PublishResult;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.publisher.ITopicPublisher;
import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.Constants;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.UnsafeBuffer;

/**
 * Vega Publisher
 * This class publish the desired number of integer to the topic
 * The publisher is in one thread, no race conditions
 */
@Slf4j
public class Publisher implements IClient {

    /** Time to wait to set up channels */
    private static int TIME_TO_SET_UP_CHANNELS = 5000;

    /** The reused buffer */
    private UnsafeBuffer sendBuffer;

    //TODO: PASAR A PARAM Numero de mensajes enviados en total
    private static int TOTAL_MESSAGES_TO_SENT = 1024;

    /** Integer to create an unique ID for each message */
    private int messageId = 0;
    /** Messages lost */
    private int errorMsgs = 0;
    /** Messages sent */
    private int sentMsgs = 0;
    /** The checksum is the sum of all the messageId published */
    private long checksum = 0;

    /**
     * Publish the desired integers
     * @param instance Vega Instance
     * @throws VegaException VegaException
     * @throws InterruptedException InterruptedException
     */
    public void run (final IVegaInstance instance)
            throws VegaException, InterruptedException {
        // Initialize the buffer
        sendBuffer = new UnsafeBuffer(ByteBuffer.allocate(128));

        // Subscribe to topics
        ITopicPublisher topicPublisher = instance.createPublisher(Constants.TOPIC_NAME);

        // Waiting for the Aeron channels to be established.
        log.info("Waiting for channels set up");
        Thread.sleep(TIME_TO_SET_UP_CHANNELS);

        log.info("Start Vega Test: Publishing data. Sending {} integers", TIME_TO_SET_UP_CHANNELS);

        // Take the starting time
        long startTime = System.currentTimeMillis();
        long startNanoTime = System.nanoTime();

        for(int i = 0; i < TOTAL_MESSAGES_TO_SENT; i++) {
            sendMsg(topicPublisher, false);
        }

        // Take the duration
        long endTime = System.currentTimeMillis() - startTime;
        long endNanoTime = System.nanoTime() - startNanoTime;

        log.info("Finnished Vega Test: Stopping Publisher");

        sendMsg(topicPublisher, true);

        log.info("****** Finnished publisher test with sendedMsgs={} and errorMsgs={}." +
                " Checksum={} ******",sentMsgs, errorMsgs, checksum);

        log.info("****** Duration endTime={}ms and endNanoTime={}ns ******",endTime, endNanoTime);

    }

    /**
     * Method to send a message and save it into the messages structure
     * @param topicPublisher topicPublisher
     */
    private void sendMsg(ITopicPublisher topicPublisher, boolean close) {

        // Create the message to send
        if(close) {
            messageId = Constants.CLOSE_ID;
        }
        else {
            checksum += messageId;
        }
        // Prepare and send the message
        sendBuffer.putInt(0, messageId);
        PublishResult result = topicPublisher.sendMsg(sendBuffer, 0, 4);

        // Increase the data
        messageId++;

        if (result == PublishResult.BACK_PRESSURED || result == PublishResult.UNEXPECTED_ERROR) {
            errorMsgs++;
        }
        else {
            sentMsgs++;
        }

    }

}
