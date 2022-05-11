package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.ClientType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class TestResults {

    /** Client type*/
    private final ClientType clientType;

    /** Messages Sent */
    private final long totalMessages;

    /** Duration of the test */
    private final long duration;

    /** Checksum of all the sent messages */
    private final long checksum;

}
