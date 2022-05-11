package com.gamusdev.lowlatency.performance.tests.aeronvega;

import com.gamusdev.lowlatency.performance.tests.aeronvega.configuration.LaunchParameters;
import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

/**
 * Parser for the command line arguments of the aeron-Vega-Test
 */
@Slf4j
class CommandLineParser
{

    /** Parameter to indicate if it is a publisher or a subscriber */
    private final Option clientType    = new Option("c", "clientType", true,
            "(Optional) Defines the client as a publisher or a subscriber. " +
                    "\"pub\" for publisher, \"sub\" for subscriber" +
                    " Default Value: subscriber");

    /** Parameter to indicate if it is a publisher or a subscriber */
    private final Option vegaConfigFilePath = new Option("f", "file", true,
            "Configuration file path");

    /** Parameter to indicate the number of messages sent / received in the test */
    private final Option sizeTest = new Option("s", "size", true,
            "Number of messages sent in this test");

    /** The command line with all the values parsed */
    private CommandLine commandLine = null;

    /**
     * Add the common command line options to the options list, 
     * it will be used to parse the command line later
     * @param options options object to add the common options to
     */
    private void addCommonCommandLineOptions(final Options options)
    {
        options.addOption(this.vegaConfigFilePath);
        options.addOption(this.clientType);
        options.addOption(this.sizeTest);
    }

    /**
     * Add the command line options including the specific parser ones, 
     * parse the command line and validate the found options
     * @param args command line arguments
     *
     * @throws ParseException exception thrown if there is a problem reading or validating the command line
     * @throws GenericAeronVegaException if the configuration is not given
     */
    LaunchParameters parseCommandLine(final String[] args)
            throws ParseException, GenericAeronVegaException {
        log.info("Parsing Aeron-Vega-Test Launcher command line arguments: {}", (Object)args);

        // Create the options
        final Options commandLineOptions = new Options();

        // Add the common command line options
        this.addCommonCommandLineOptions(commandLineOptions);

        // Parse the command line
        final org.apache.commons.cli.CommandLineParser commandLineParser = new PosixParser();
        
        // Create the command line
        this.commandLine = commandLineParser.parse(commandLineOptions, args);
        
        // Verify the command line arguments, both the common and the additional ones
        return this.parseAndValidateCommandLine();
    }

    /**
     * Validate the input command line arguments parsed from the command line
     */
    private LaunchParameters parseAndValidateCommandLine() throws GenericAeronVegaException {
        final String configFilePath = this.getCmdStringOption(this.vegaConfigFilePath);
        final String clientType = this.getCmdStringOption(this.clientType);
        final String sizeTest = this.getCmdStringOption(this.sizeTest);

        // Validate the parameters
        return new LaunchParameters(configFilePath, clientType, sizeTest);
    }

    /**
     * Return the String option value from the command line given the representing option
     *
     * @param option the representing option of the command line
     * @return the value of the option, exception if unsettled
     */
    private String getCmdStringOption(final Option option)
    {
        if(commandLine.hasOption(option.getOpt()))
        {
            return commandLine.getOptionValue(option.getOpt()).trim();
        }

        return null;
    }

}
