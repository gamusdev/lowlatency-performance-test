package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

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
    private static final int TIME_TO_SET_UP_CHANNELS = 5000;

    /** The reused buffer */
    private UnsafeBuffer sendBuffer;


    //TODO: PASAR A PARAM Numero de mensajes enviados en total
    // Un int son 4 bytes
    // Un Mega: 1048576
    //private static int TOTAL_MESSAGES_TO_SENT = 1_048_576; // 4 x 1 Mb
    //private static int TOTAL_MESSAGES_TO_SENT = 10_485_760; // 4 x 10 Mb
    private static int TOTAL_MESSAGES_TO_SENT = 105_000_000; // 4 x 100 Mb

    /** Messages sent */
    private int sentMsgs = 0;
    /** The checksum is the sum of all the messageId published */
    private long checksum = 0;

    /**
     * Publish the desired integers
     * @param instance Vega Instance
     * @throws VegaException Vega Exception
     * @throws InterruptedException Interrupted Exception
     */
    public void run (final IVegaInstance instance)
            throws VegaException, InterruptedException {
        // Initialize the buffer
        sendBuffer = new UnsafeBuffer(ByteBuffer.allocate(128));

        // Subscribe to topics
        ITopicPublisher topicPublisher = instance.createPublisher(Constants.TOPIC_NAME);

        // Waiting for the Aeron channels to be established.
        Thread.sleep(TIME_TO_SET_UP_CHANNELS);

        log.info("Start Vega Test: Publishing data. Sending {} integers", TOTAL_MESSAGES_TO_SENT);

        // Take the starting time
        long startTime = System.currentTimeMillis();

        Stream.iterate(1,                   // start
                n -> n < TOTAL_MESSAGES_TO_SENT,// Predicate to finish
                n -> n + 1                      // Increment
        ).forEach( id -> sendMsg(topicPublisher, id) );

        // Take the duration
        long durationTime = System.currentTimeMillis() - startTime;

        log.info("Finnished Vega Test: Stopping Publisher");

        Thread.sleep(500);
        sendMsg(topicPublisher, Constants.CLOSE_ID);

        log.info("****** Finnished publisher test with sentMsgs={}." +
                " Checksum={}, numBackPresure={} ******",sentMsgs, checksum);

        log.info("****** Duration endTime={}ms ******\n\n", durationTime);

    }

    /**
     * Method to send a message and save it into the messages structure
     * @param topicPublisher topicPublisher
     */
    private void sendMsg(ITopicPublisher topicPublisher, int messageId) {

        // Prepare and send the message
        sendBuffer.putInt(0, messageId);
        PublishResult result = topicPublisher.sendMsg(sendBuffer, 0, 4);

        // Check if we have back pressure
        if (BackPressureManager.checkAndControl(result)) {
            //Resend the message
            log.info("{}. Resend {}", result, messageId);
            sendMsg(topicPublisher, messageId);
        }
        else {
            sentMsgs++;
            // Create the message to send
            if(messageId != Constants.CLOSE_ID) {
                checksum += messageId;
            }
        }

    }

}
