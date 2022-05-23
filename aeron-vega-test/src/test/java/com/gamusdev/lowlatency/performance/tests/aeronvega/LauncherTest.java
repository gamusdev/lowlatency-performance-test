package com.gamusdev.lowlatency.performance.tests.aeronvega;

import com.gamusdev.lowlatency.performance.tests.aeronvega.exception.GenericAeronVegaException;
import com.gamusdev.lowlatency.performance.tests.aeronvega.executor.ITestExecutor;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.ServiceLoader;

public class LauncherTest {

    /**
     * Executes the test when the testExecutor is found
     * @throws ParseException Exception
     * @throws GenericAeronVegaException Exception
     */
    @Test
    public void mainOkTest() throws ParseException, GenericAeronVegaException {
        // When
        ITestExecutor testExecutorMock = Mockito.mock(ITestExecutor.class);
        ServiceLoader<ITestExecutor> serviceLoaderMock = Mockito.mock(ServiceLoader.class);
        String[] args = {"param"};
        Optional optional = Optional.of(testExecutorMock);

        try (MockedStatic<ServiceLoader> serviceLoaderFactoryMock = Mockito.mockStatic(ServiceLoader.class)) {
            serviceLoaderFactoryMock.when( () -> ServiceLoader.load(ITestExecutor.class)).thenReturn(serviceLoaderMock);
            Mockito.when(serviceLoaderMock.findFirst()).thenReturn(optional);

            // Then
            Launcher.main(args);

            // Verify
            serviceLoaderFactoryMock.verify(() -> ServiceLoader.load(ITestExecutor.class));
            Mockito.verify(testExecutorMock, Mockito.times(1)).executeTest(args);
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
        ServiceLoader serviceLoaderMock = Mockito.mock(ServiceLoader.class);
        String[] args = {"param"};
        Optional optional = Optional.empty();

        try (MockedStatic<ServiceLoader> serviceLoaderFactoryMock = Mockito.mockStatic(ServiceLoader.class)) {
            serviceLoaderFactoryMock.when( () -> ServiceLoader.load(ITestExecutor.class)).thenReturn(serviceLoaderMock);
            Mockito.when(serviceLoaderMock.findFirst()).thenReturn(optional);

            // Then
            Launcher.main(args);

            // Verify
            serviceLoaderFactoryMock.verify(() -> ServiceLoader.load(ITestExecutor.class));
        }
    }
}
