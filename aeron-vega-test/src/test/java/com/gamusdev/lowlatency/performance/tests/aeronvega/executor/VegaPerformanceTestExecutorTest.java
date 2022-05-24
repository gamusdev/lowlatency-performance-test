package com.gamusdev.lowlatency.performance.tests.aeronvega.executor;

import com.bbva.kyof.vega.exception.VegaException;
import com.bbva.kyof.vega.protocol.IVegaInstance;
import com.bbva.kyof.vega.protocol.VegaInstance;
import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.ClientFactory;
import com.gamusdev.lowlatency.performance.tests.aeronvega.clients.IClient;
import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import com.gamusdev.lowlatency.performance.tests.aeronvega.model.TestResults;
import com.gamusdev.lowlatency.performance.tests.aeronvega.parser.ICommandLineParser;
import com.gamusdev.lowlatency.performance.tests.aeronvega.parser.LaunchParameters;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;
import java.util.ServiceLoader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class VegaPerformanceTestExecutorTest {

    private static final String[] ARGS = {"param"};
    private static final String CONFIG_FILE_PATH = "PATH";
    private static final int SIZE = 0;

    @Mock
    private ServiceLoader<ICommandLineParser> serviceLoaderMock;

    @Mock
    private IVegaInstance vegaInstanceMock;

    @Mock
    private LaunchParameters launchParametersMock;

    @Mock
    private IClient client;

    @Mock
    private TestResults testResults;

    @InjectMocks
    private VegaPerformanceTestExecutor executor;

    @Test
    public void executeTestOkTest() throws ParseException, GenericAeronVegaException, VegaException, InterruptedException, IOException {
        // ******* When
        // Prepare CommandLineParser
        final ICommandLineParser commandLineParserMock = Mockito.mock(ICommandLineParser.class);
        final Optional<ICommandLineParser> optional = Optional.of(commandLineParserMock);

        // Static ServiceLoader && VegaInstance
        try (MockedStatic<ServiceLoader> serviceLoaderFactoryMock = Mockito.mockStatic(ServiceLoader.class);
             MockedStatic<VegaInstance> vegaInstanceFactoryMock = Mockito.mockStatic(VegaInstance.class)
             ) {

            // Load CommandLineParser
            serviceLoaderFactoryMock.when( () -> ServiceLoader.load(ICommandLineParser.class)).thenReturn(serviceLoaderMock);
            Mockito.when(serviceLoaderMock.findFirst()).thenReturn(optional);

            // Service Loader Mock
            serviceLoaderFactoryMock.when( () -> ServiceLoader.load(IClient.class)).thenReturn(serviceLoaderMock);

            // LaunchParameters
            Mockito.when(commandLineParserMock.parseCommandLine(ARGS)).thenReturn(launchParametersMock);
            Mockito.when(launchParametersMock.getVegaConfigFilePath()).thenReturn(CONFIG_FILE_PATH);
            Mockito.when(launchParametersMock.getSizeTest()).thenReturn(SIZE);

            // Load VegaInstance
            vegaInstanceFactoryMock.when( () -> VegaInstance.createNewInstance(any())).thenReturn( vegaInstanceMock );

            // Static ClientFactory
            try (MockedStatic<ClientFactory> clientFactoryMock = Mockito.mockStatic(ClientFactory.class) ) {

                // Load ClientFactory
                clientFactoryMock.when( () -> ClientFactory.getInstance(any())).thenReturn( client );

                // IClient
                Mockito.when(client.run(vegaInstanceMock, SIZE)).thenReturn(testResults);

                // ******* Then
                executor.executeTest(ARGS);

                // ******* Verify
                // ClientFactory && client.run
                serviceLoaderFactoryMock.verify(() -> ServiceLoader.load(IClient.class));
                clientFactoryMock.verify(() -> ClientFactory.getInstance(any()));
                Mockito.verify(client, Mockito.times(1)).run(eq(vegaInstanceMock), eq(SIZE));

            }

            // ******* Verify
            // CommandLineParser
            serviceLoaderFactoryMock.verify(() -> ServiceLoader.load(ICommandLineParser.class));
            Mockito.verify(commandLineParserMock, Mockito.times(1)).parseCommandLine(ARGS);

            // LaunchParameters
            Mockito.verify(launchParametersMock, Mockito.times(1)).getVegaConfigFilePath();

            // VegaInstance
            vegaInstanceFactoryMock.verify(() -> VegaInstance.createNewInstance(any()));
            Mockito.verify(vegaInstanceMock, Mockito.times(1)).close();

        }
    }
}
