package com.gamusdev.lowlatency.performance.tests.aeronvega.configuration;

import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Parameters for the aeron-Vega-Test Launcher
 */
@ToString
public class LaunchParameters {

    /** Parameter indicating the selected client type (PUB or SUB) */
    @Getter
    private ClientType clientType = ClientType.SUB;

    @Getter
    private TestType testType = TestType.ALL;

    @Getter
    private String vegaConfigFilePath;

    /**
     * Constructor
     * Validates the ConfigFilePath and the clientType param or left the default as SUB
     * @param pConfigFilePath Vega xml file configuration
     * @param pClientType PUB / SUB mode
     */
    public LaunchParameters (String pConfigFilePath, String pClientType, String pTestType)
            throws GenericAeronVegaException {

        // Check the test type is given and valid. If yes, replace the value
        if (EnumUtils.isValidEnumIgnoreCase(TestType.class, pTestType)) {
            this.testType = TestType.valueOf(pTestType.toUpperCase(Locale.ROOT));
        }

        // Check if client type is given and valid. If yes, replace the value
        if (EnumUtils.isValidEnumIgnoreCase(ClientType.class, pClientType)) {
            this.clientType = ClientType.valueOf(pClientType.toUpperCase(Locale.ROOT));
        }

        // Test if VegaTest will be executed and vegaConfigFilePath is a valid path to an XML file
        if ( TestType.ALL == this.testType || TestType.VEGA == this.testType) {

            if (StringUtils.isEmpty(pConfigFilePath) || Files.notExists(Path.of(pConfigFilePath))) {
                throw new GenericAeronVegaException("The parameter vegaConfigFilePath must be valid");
            }

            // Set Vega config file
            this.vegaConfigFilePath = pConfigFilePath;
        }
    }

}
