package com.gamusdev.lowlatency.performance.tests.aeronvega.clients;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.msg.PublishResult;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.publisher.ITopicPublisher;
import com.gamusdev.lowlatency.performance.tests.aeronvega.utils.Constants;
import com.gamusdev.lowlatency.performance.tests.aeronvega.utils.BackPressureManager;
import com.gamusdev.lowlatency.performance.tests.aeronvega.model.TestResults;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.UnsafeBuffer;

/**
 * Vega Publisher
 * This class publish the desired number of integer to the topic
 * The publisher is in one thread, no race conditions
 */
@Slf4j
public class Publisher implements IClient {

    /** Enum ClientType to indicate PUB (publisher) */
    public final static ClientTypeEnum CLIENT_TYPE = ClientTypeEnum.PUB;

    /** Time to wait to set up channels */
    private final static int TIME_TO_WAIT = 3000;

    /** The reused buffer */
    private final UnsafeBuffer sendBuffer;

    /** The checksum is the sum of all the messageId published */
    private long checksum = 0;

    /**
     * Constructor
     */
    public Publisher() {
        super();

        // Initialize the buffer
        sendBuffer = new UnsafeBuffer(ByteBuffer.allocate(4));
    }

    /**
     * Establish the publisher channel
     */
    private ITopicPublisher createChannels(final IVegaInstance instance)
            throws VegaException, InterruptedException  {
        // Subscribe to topic as publisher
        ITopicPublisher topicPublisher = instance.createPublisher(Constants.TOPIC_NAME);

        // Waiting for the channels to be established.
        Thread.sleep(TIME_TO_WAIT);

        return topicPublisher;
    }

    /**
     * Method to clear all counters and checksum
     */
    private void cleanCounters() {
        checksum = 0;
    }

    /**
     * When starts, the JVN creates some optimizations, but it needs some training.
     * Number of messages sent to warn up the JVM.
     * The purpose of this method is to train the JVM
     * */
    private void warnUpJVM(ITopicPublisher topicPublisher) throws InterruptedException {
        log.info("****** Start Vega warm up ******");

        // Send the training messages
        Stream.iterate(1,                   // start
                n -> n <= WARN_UP_MESSAGES,// Predicate to finish
                n -> n + 1                      // Increment
        ).forEach( id -> sendMsg(topicPublisher, id) );

        // Initialize the counters
        cleanCounters();

        log.info("****** Finished Vega warm up ******");

        // Give some time to the receiver to consume the warm messages
        Thread.sleep(TIME_TO_WAIT);
    }

    /**
     * Execute the test
     * @param topicPublisher the topicPublisher where send the messages
     * @param sizeTest the number of messages to send in the test
     * @return TestResults with the results
     */
    private TestResults executeTest(ITopicPublisher topicPublisher, int sizeTest) {

        log.info("****** Start Vega Test: Publishing data. Sending {} integers ******", sizeTest);

        // Take the starting time
        long startTime = System.currentTimeMillis();

        // Send the test messages
        Stream.iterate(1,                   // start
                n -> n <= sizeTest,// Predicate to finish
                n -> n + 1                      // Increment
        ).forEach( id -> sendMsg(topicPublisher, id) );

        // Take the duration
        long durationTime = System.currentTimeMillis() - startTime;

        log.info("****** Finished Vega Test: Duration endTime={}ms ******", durationTime);

        // return the results
        return TestResults.builder()
                .totalMessages(sizeTest)
                .duration(durationTime)
                .checksum(checksum)
                .build();
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
            sendMsg(topicPublisher, messageId);
        }
        else if(messageId != CLOSE_ID) {
            // Add the id to the checksum
            checksum += messageId;
        }

    }

    /**
     * Close the channel.
     * To close, a signal is sent.
     */
    private void close(ITopicPublisher topicPublisher) throws InterruptedException {
        // Take some time to avoid Back Pressure and send the signal to close the channel
        Thread.sleep(500);
        sendMsg(topicPublisher, CLOSE_ID);
    }

    /**
     * Publish the desired integers
     * @param instance Vega Instance
     * @param sizeTest Number of messages to send in the test
     * @throws VegaException Vega Exception
     * @throws InterruptedException Interrupted Exception
     */
    public TestResults run (final IVegaInstance instance, int sizeTest)
            throws VegaException, InterruptedException {

        // Create the channel
        ITopicPublisher topicPublisher = createChannels(instance);

        warnUpJVM(topicPublisher);

        // Execute the tests
        var testResults = executeTest(topicPublisher, sizeTest);

        // Close the channels.
        close(topicPublisher);

        return testResults;
    }

}
