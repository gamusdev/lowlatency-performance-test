package com.gamusdev.lowlatency.performance.tests.brokers.interceptor;

import com.gamusdev.lowlatency.performance.tests.brokers.configuration.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * OutputChannelInterceptor class.
 * Used to stop the app.
 */
@Slf4j
@Service
@GlobalChannelInterceptor
public class PerformanceChannelInterceptor implements org.springframework.messaging.support.ChannelInterceptor {

    /**
     * Application context
     */
    private ConfigurableApplicationContext context;

    /**
     * Final signal in bytes to compare with the messages payload.
     */
    private final byte[] CloseIdBytes;

    /** Start time, when te first message is received */
    private long startTime = 0;

    /**
     * Constructor
     */
    @Autowired
    public PerformanceChannelInterceptor(ConfigurableApplicationContext context) {
        this.context = context;
        this.CloseIdBytes = Config.CLOSE_ID.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Compare the payload message with the close signal. When the close signal is sent, ends the test
     * @param message message
     * @param channel channel used
     * @param sent boolean to indicate if the message was sent correctly
     * @param ex exception
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
        // If it is the first data, save the start time
        if (this.startTime == 0) {
            this.startTime = System.currentTimeMillis();
        }

        if(Arrays.equals(this.CloseIdBytes, (byte[]) message.getPayload())) {

            // Take the duration. Decrement the signal message from the counter
            long duration = System.currentTimeMillis() - startTime;
            log.info("****** Broker Test Finished: Duration: {} ******", duration);

            context.close();
        }
    }
}
