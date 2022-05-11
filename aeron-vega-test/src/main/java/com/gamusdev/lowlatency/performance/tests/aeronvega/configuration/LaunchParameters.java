package com.gamusdev.lowlatency.performance.tests.aeronvega.configuration;

import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Parameters for the aeron-Vega-Test Launcher
 */
@Slf4j
@ToString
public class LaunchParameters {

    /** Parameter indicating the selected client type (PUB or SUB) */
    @Getter
    private ClientType clientType = ClientType.SUB;

    /** Vega configuration XML file path */
    @Getter
    private String vegaConfigFilePath;

    /** The number of messages sent / received in this test */
    @Getter
    private int sizeTest = 1_000_000;

    /**
     * Constructor
     * Validates the ConfigFilePath and the clientType param or left the default as SUB
     * @param pConfigFilePath Vega xml file configuration
     * @param pClientType PUB / SUB mode
     */
    public LaunchParameters (String pConfigFilePath, String pClientType, String pSizeTest)
            throws GenericAeronVegaException {

        // Check if client type is given and valid. If yes, replace the value
        if (EnumUtils.isValidEnumIgnoreCase(ClientType.class, pClientType)) {
            this.clientType = ClientType.valueOf(pClientType.toUpperCase(Locale.ROOT));
        }

        // Check if the vega config xml file exists
        if (StringUtils.isBlank(pConfigFilePath) || Files.notExists(Path.of(pConfigFilePath))) {
            throw new GenericAeronVegaException("The parameter vegaConfigFilePath must be valid");
        }

        // Set Vega config file
        this.vegaConfigFilePath = pConfigFilePath;

        // Check if the size of the test is given and valid
        if (StringUtils.isNotBlank( pSizeTest) ) {
            try {
                this.sizeTest = Integer.parseUnsignedInt(pSizeTest);
            }
            catch (NumberFormatException e) {
                log.error("Parameter sizeTest not valid {}. Executing test with default", pSizeTest);
            }
        }

    }

}
