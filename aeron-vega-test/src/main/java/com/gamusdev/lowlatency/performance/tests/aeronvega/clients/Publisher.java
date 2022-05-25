package com.gamusdev.lowlatency.performance.tests.aeronvega.clients;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.msg.PublishResult;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.publisher.ITopicPublisher;
import com.gamusdev.lowlatency.performance.tests.aeronvega.utils.Checksum;
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

    /** Time to wait to set up channels */
    private static final int TIME_TO_WAIT = 3000;

    /** Size of the data sent (one integer)*/
    private static final int PAYLOAD_SIZE = 4;

    /** Enum ClientType to indicate PUB (publisher) */
    private final ClientTypeEnum clientType = ClientTypeEnum.PUB;

    /** The reused buffer */
    private final UnsafeBuffer sendBuffer;

    /**
     * Constructor
     */
    public Publisher() {
        super();

        // Initialize the buffer
        sendBuffer = new UnsafeBuffer(ByteBuffer.allocate(PAYLOAD_SIZE));
    }

    /**
     * Establish the publisher channel
     */
    private ITopicPublisher createChannels(final IVegaInstance instance)
            throws VegaException, InterruptedException  {
        // Subscribe to topic as publisher
        final ITopicPublisher topicPublisher = instance.createPublisher(Constants.TOPIC_NAME);

        // Waiting for the Aeron channels to be established.
        Thread.sleep(TIME_TO_WAIT);

        return topicPublisher;
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
        final long startTime = System.currentTimeMillis();

        // Send the test messages
        Stream.iterate(1,                   // start
                n -> n <= sizeTest,// Predicate to finish
                n -> n + 1                      // Increment
        ).forEach( id -> sendMsg(topicPublisher, id) );

        // Take the duration
        final long durationTime = System.currentTimeMillis() - startTime;

        log.info("****** Finished Vega Test: Duration endTime={}ms ******", durationTime);

        // return the results
        return TestResults.builder()
                .totalMessages(sizeTest)
                .duration(durationTime)
                .checksum(Checksum.getChecksum(sizeTest))
                .build();
    }

    /**
     * Method to send a message and save it into the messages structure
     * @param topicPublisher topicPublisher
     */
    private void sendMsg(ITopicPublisher topicPublisher, int messageId) {

        // Prepare and send the message
        sendBuffer.putInt(0, messageId);
        final PublishResult result = topicPublisher.sendMsg(sendBuffer, 0, PAYLOAD_SIZE);

        // Check if we have back pressure
        if (BackPressureManager.checkAndControl(result)) {
            //Resend the message
            sendMsg(topicPublisher, messageId);
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
    public TestResults run (final IVegaInstance instance, final int sizeTest)
            throws VegaException, InterruptedException {

        // Create the channel
        final ITopicPublisher topicPublisher = createChannels(instance);

        // Execute the tests
        final var testResults = executeTest(topicPublisher, sizeTest);

        // Close the channels.
        close(topicPublisher);

        return testResults;
    }

    /**
     * Returns the ClientTypeEnum of the service
     * @return ClientTypeEnum of the service
     */
    public ClientTypeEnum getClientType() {
        return clientType;
    }
}
