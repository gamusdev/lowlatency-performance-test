package com.gamusdev.lowlatency.performance.tests.aeronvega;

import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import com.gamusdev.lowlatency.performance.tests.aeronvega.executor.ITestExecutor;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.ServiceLoader;

@ExtendWith(MockitoExtension.class)
public class LauncherTest {

    private static final String[] ARGS = {"param"};

    @Mock
    private ServiceLoader<ITestExecutor> serviceLoaderMock;

    /**
     * Executes the test when the testExecutor is found
     * @throws ParseException Exception
     * @throws GenericAeronVegaException Exception
     */
    @Test
    public void mainOkTest() throws ParseException, GenericAeronVegaException {
        // When
        final ITestExecutor testExecutorMock = Mockito.mock(ITestExecutor.class);

        final Optional<ITestExecutor> optional = Optional.of(testExecutorMock);

        try (MockedStatic<ServiceLoader> serviceLoaderFactoryMock = Mockito.mockStatic(ServiceLoader.class)) {
            serviceLoaderFactoryMock.when( () -> ServiceLoader.load(ITestExecutor.class)).thenReturn(serviceLoaderMock);
            Mockito.when(serviceLoaderMock.findFirst()).thenReturn(optional);

            // Then
            Launcher.main(ARGS);

            // Verify
            serviceLoaderFactoryMock.verify(() -> ServiceLoader.load(ITestExecutor.class));
            Mockito.verify(testExecutorMock, Mockito.times(1)).executeTest(ARGS);
        }
    }

    /**
     * Executes the test when the testExecutor is NOT found
     * @throws ParseException Exception
     * @throws GenericAeronVegaException Exception
     */
    @Test
    public void mainExecutorNotFoundTest() throws ParseException, GenericAeronVegaException {
        // When
        final Optional<ITestExecutor> optional = Optional.empty();

        try (MockedStatic<ServiceLoader> serviceLoaderFactoryMock = Mockito.mockStatic(ServiceLoader.class)) {
            serviceLoaderFactoryMock.when( () -> ServiceLoader.load(ITestExecutor.class)).thenReturn(serviceLoaderMock);
            Mockito.when(serviceLoaderMock.findFirst()).thenReturn(optional);

            // Then
            Launcher.main(ARGS);

            // Verify
            serviceLoaderFactoryMock.verify(() -> ServiceLoader.load(ITestExecutor.class));
        }
    }
}
