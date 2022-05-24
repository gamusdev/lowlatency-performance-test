package com.gamusdev.lowlatency.performance.tests.aeronvega.parser;

import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.IClient;
import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class VegaCommandLineParserTest {

    private static final int NUM_PARAMS = 3;

    @InjectMocks
    private VegaCommandLineParser vegaCommandLineParser;

    @Test
    public void addCommonCommandLineOptionsTest() {
        // When
        final Options optionsMock = Mockito.mock(Options.class);

        // Then
        vegaCommandLineParser.addCommonCommandLineOptions(optionsMock);

        // Verify
        Mockito.verify(optionsMock, Mockito.times(NUM_PARAMS)).addOption(any(Option.class));
    }

    @Test
    public void parseAndValidateCommandLineTest() throws GenericAeronVegaException {
        // When
        final String configFilePath = "./";
        final String clientType = "pub";
        final Integer sizeTest = 10;
        final CommandLine commandLineMock = Mockito.mock(CommandLine.class);

        Mockito.when(commandLineMock.hasOption(any())).thenReturn(true);
        Mockito.when(commandLineMock.getOptionValue("c")).thenReturn(clientType);
        Mockito.when(commandLineMock.getOptionValue("f")).thenReturn(configFilePath);
        Mockito.when(commandLineMock.getOptionValue("s")).thenReturn(sizeTest.toString());

        // Then
        final LaunchParameters parameters = vegaCommandLineParser.parseAndValidateCommandLine(commandLineMock);

        // Verify
        Mockito.verify(commandLineMock, Mockito.times(NUM_PARAMS)).hasOption(any());
        Mockito.verify(commandLineMock, Mockito.times(NUM_PARAMS)).getOptionValue(any());

        Assertions.assertEquals(configFilePath, parameters.getVegaConfigFilePath());
        Assertions.assertEquals(IClient.ClientTypeEnum.valueOf(clientType.toUpperCase(Locale.ROOT)), parameters.getClientTypeEnum());
        Assertions.assertEquals(sizeTest, parameters.getSizeTest());

    }
}
