package com.gamusdev.lowlatency.performance.tests.aeronvega.parser;

import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

/**
 * Parser for the command line arguments of the aeron-Vega-Test
 */
@Slf4j
public class VegaCommandLineParser implements ICommandLineParser {

    /** Parameter to indicate if it is a publisher or a subscriber */
    private final Option clientType    = new Option("c", "clientType", true,
            "(Optional) Defines the client as a publisher or a subscriber. " +
                    "\"pub\" for publisher, \"sub\" for subscriber. Default Value: subscriber");

    /** Parameter to indicate the Vega config file path */
    private final Option vegaConfigFilePath = new Option("f", "file", true,
            "Configuration file path");

    /** Parameter to indicate the number of messages sent / received in the test */
    private final Option sizeTest = new Option("s", "size", true,
            "Number of messages sent in this test");

    /**
     * Add the common command line options to the options list, 
     * it will be used to parse the command line later
     * @param options options object to add the common options to
     */
    public void addCommonCommandLineOptions(final Options options)
    {
        options.addOption(this.vegaConfigFilePath);
        options.addOption(this.clientType);
        options.addOption(this.sizeTest);
    }

    /**
     * Validate the input command line arguments parsed from the command line
     * @param commandLine the commandLine
     * @return LaunchParameters with the desired parameters
     */
    public LaunchParameters parseAndValidateCommandLine(final CommandLine commandLine)
            throws GenericAeronVegaException {
        final String configFilePath = this.getCmdStringOption(commandLine, this.vegaConfigFilePath);
        final String clientType = this.getCmdStringOption(commandLine, this.clientType);
        final String sizeTest = this.getCmdStringOption(commandLine, this.sizeTest);

        // Validate the parameters
        return new LaunchParameters(configFilePath, clientType, sizeTest);
    }
}
