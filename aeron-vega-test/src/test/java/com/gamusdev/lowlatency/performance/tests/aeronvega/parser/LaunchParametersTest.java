package com.gamusdev.lowlatency.performance.tests.aeronvega.parser;

import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.IClient;
import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

@ExtendWith(MockitoExtension.class)
public class LaunchParametersTest {

    @Test
    public void launchParametersOkTest() throws GenericAeronVegaException {
        // When
        final String configFilePath = "./";
        final String clientType = "pub";
        final Integer sizeTest = 10;

        // Then
        final LaunchParameters launchParameters = new LaunchParameters(configFilePath, clientType, sizeTest.toString());

        // Verify
        Assertions.assertEquals(configFilePath, launchParameters.getVegaConfigFilePath());
        Assertions.assertEquals(IClient.ClientTypeEnum.PUB, launchParameters.getClientTypeEnum());
        Assertions.assertEquals(sizeTest, launchParameters.getSizeTest() );

        Assertions.assertTrue(launchParameters.toString().contains(configFilePath));
        Assertions.assertTrue(launchParameters.toString().contains(clientType.toUpperCase(Locale.ROOT)));
        Assertions.assertTrue(launchParameters.toString().contains(sizeTest.toString()));
    }

    @Test
    public void launchParametersBadClientTypeTest() throws GenericAeronVegaException {
        // When
        final String configFilePath = "./";
        final String clientType = "";
        final Integer sizeTest = 10;

        // Then
        final LaunchParameters launchParameters = new LaunchParameters(configFilePath, clientType, sizeTest.toString());

        // Verify
        Assertions.assertEquals(configFilePath, launchParameters.getVegaConfigFilePath());
        Assertions.assertEquals(sizeTest, launchParameters.getSizeTest() );
        //Test the default
        Assertions.assertEquals(IClient.ClientTypeEnum.SUB, launchParameters.getClientTypeEnum());
    }

    @Test
    public void launchParametersBadSizeTest() throws GenericAeronVegaException {
        // When
        final String configFilePath = "./";
        final String clientType = "SUB";
        final String sizeTest = "10a";

        // Then
        final LaunchParameters launchParameters = new LaunchParameters(configFilePath, clientType, sizeTest);

        // Verify
        Assertions.assertEquals(configFilePath, launchParameters.getVegaConfigFilePath());
        Assertions.assertEquals(IClient.ClientTypeEnum.SUB, launchParameters.getClientTypeEnum());
        //Test the default
        Assertions.assertEquals(1_000_000, launchParameters.getSizeTest() );
    }

    @Test
    public void launchParametersBadPathTest() throws GenericAeronVegaException {
        // When
        final String configFilePath = "badPath";
        final String clientType = "SUB";
        final String sizeTest = "10";

        // Then
        Assertions.assertThrows(GenericAeronVegaException.class,
                () -> new LaunchParameters(configFilePath, clientType, sizeTest));

    }
}
