package com.gamusdev.lowlatency.performance.tests.brokers.interceptor;

import com.gamusdev.lowlatency.performance.tests.brokers.configuration.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * OutputChannelInterceptor class
 * This interceptor is loaded only in the output channel.
 * Used to stop the sender.
 */
@Slf4j
@Service
@GlobalChannelInterceptor(patterns={"*-out-0"})
public class OutputChannelInterceptor implements ChannelInterceptor {

    /**
     * Final signal in bytes to compare with the messages sent.
     */
    private final byte[] CloseIdBytes;

    /**
     * Constructor
     */
    public OutputChannelInterceptor() {
        this.CloseIdBytes = Config.CLOSE_ID.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Compare the output message with the close signal. When the close signal is sent, ends the test
     * @param message message sent
     * @param channel channel used
     * @param sent boolean to indicate if the message was sent correctly
     * @param ex exception
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {

        if(Arrays.equals(this.CloseIdBytes, (byte[]) message.getPayload())) {
            log.info("*******Finish sending integers");
            System.exit(0);
        }

    }

}
