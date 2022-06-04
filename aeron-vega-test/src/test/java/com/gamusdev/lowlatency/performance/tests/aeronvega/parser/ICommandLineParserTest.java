package com.gamusdev.lowlatency.performance.tests.aeronvega.parser;

import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ICommandLineParserTest {

    private ICommandLineParser iCommandLineParser = spy(ICommandLineParser.class);

    @Test
    public void getCmdStringOptionDoesNotExistTest() {
        // When
        final CommandLine commandLineMock = Mockito.mock(CommandLine.class);
        final Option option = Mockito.mock(Option.class);
        Mockito.when(commandLineMock.hasOption(any())).thenReturn(false);

        // Then && Verify
        Assertions.assertNull(iCommandLineParser.getCmdStringOption(commandLineMock, option));
    }

    @Test
    public void getCmdStringOptionExistTest() {
        // When
        final String result = "result";
        final CommandLine commandLineMock = Mockito.mock(CommandLine.class);
        final Option option = Mockito.mock(Option.class);
        Mockito.when(commandLineMock.hasOption(any())).thenReturn(true);
        Mockito.when(commandLineMock.getOptionValue(any())).thenReturn(result);

        // Then && Verify
        Assertions.assertEquals(result, iCommandLineParser.getCmdStringOption(commandLineMock, option));
    }

    @Test
    public void parseCommandLineTest() throws ParseException, GenericAeronVegaException {
        // When
        final String[] args = {"ARGS"};

        // Then
        iCommandLineParser.parseCommandLine(args);

        // Verify
        Mockito.verify(iCommandLineParser, times(1)).addCommonCommandLineOptions(any());
        Mockito.verify(iCommandLineParser, times(1)).parseAndValidateCommandLine(any());
    }
}
