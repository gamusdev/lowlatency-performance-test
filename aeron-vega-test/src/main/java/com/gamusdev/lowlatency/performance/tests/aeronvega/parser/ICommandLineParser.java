package com.gamusdev.lowlatency.performance.tests.aeronvega.parser;

import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import org.apache.commons.cli.*;

/**
 * Interface with common methods for CommandLineParser.
 * Template Pattern
 */
public interface ICommandLineParser {

    /**
     * Add the common command line options to the options list,
     * it will be used to parse the command line later
     * @param options options object to add the common options to
     */
    void addCommonCommandLineOptions(final Options options);

    /**
     * Validate the input command line arguments parsed from the command line
     * @param commandLine the commandLine
     * @return LaunchParameters with the desired parameters
     */
    LaunchParameters parseAndValidateCommandLine(final CommandLine commandLine)
            throws GenericAeronVegaException;

    /**
     * Add the command line options including the specific parser ones,
     * parse the command line and validate the found options
     * @param args command line arguments
     * @return LaunchParameters with the desired parameters
     *
     * @throws ParseException exception thrown if there is a problem reading or validating the command line
     * @throws GenericAeronVegaException if the configuration is not given
     */
    default LaunchParameters parseCommandLine(final String[] args)
            throws ParseException, GenericAeronVegaException {

        // Create the options
        final Options commandLineOptions = new Options();

        // Add the common command line options
        addCommonCommandLineOptions(commandLineOptions);

        // Parse the command line
        final org.apache.commons.cli.CommandLineParser commandLineParser = new PosixParser();

        // Create the command line
        CommandLine commandLine = commandLineParser.parse(commandLineOptions, args);

        // Verify the command line arguments, both the common and the additional ones
        return this.parseAndValidateCommandLine(commandLine);
    }

    /**
     * Return the String option value from the command line given the representing option
     *
     * @param option the representing option of the command line
     * @return the value of the option, exception if unsettled
     */
    default String getCmdStringOption(final CommandLine commandLine, final Option option)
    {
        if(commandLine.hasOption(option.getOpt()))
        {
            return commandLine.getOptionValue(option.getOpt()).trim();
        }

        return null;
    }
}
