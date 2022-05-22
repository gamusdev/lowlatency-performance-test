package com.gamusdev.lowlatency.performance.tests.brokers.interceptor;

import com.gamusdev.lowlatency.performance.tests.brokers.configuration.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PerformanceChannelInterceptorTest {

    @Mock
    private ConfigurableApplicationContext context;

    @InjectMocks
    private PerformanceChannelInterceptor interceptor;

    @Test
    public void afterSendCompletionStartingTest() {
        // When
        final Message<?> message = mock(Message.class);
        final MessageChannel channel = mock(MessageChannel.class);

        // Then
        interceptor.afterSendCompletion(message, channel, true, null);

        // Verify:
        // startTime is initialized
        final String starTime = Objects.requireNonNull(ReflectionTestUtils.getField(interceptor, "startTime")).toString();
        Assertions.assertTrue(Long.parseLong(starTime) > 0 );

        // context is not closed
        verify(context, times(0)).close();
    }

    @Test
    public void afterSendCompletionEndingTest() {
        // When
        final long mockStartTime = 1;
        ReflectionTestUtils.setField(interceptor, "startTime", mockStartTime);

        final Message<byte[]> message = mock(Message.class);
        when(message.getPayload()).thenReturn( Config.CLOSE_ID.toString().getBytes(StandardCharsets.UTF_8) );

        final MessageChannel channel = mock(MessageChannel.class);

        // Then
        interceptor.afterSendCompletion(message, channel, true, null);

        // Verify:
        // startTime is initialized
        final String starTime = Objects.requireNonNull(ReflectionTestUtils.getField(interceptor, "startTime")).toString();
        Assertions.assertEquals(mockStartTime, Long.parseLong(starTime) );

        // context is not closed
        verify(context, times(1)).close();
    }
}
