package com.gamusdev.lowlatency.performance.tests.aeronvega.clients;

import com.gamusdev.lowlatency.performance.tests.aeronvega.executor.ITestExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ServiceLoader;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class ClientFactoryTest {

    @Mock
    private ServiceLoader<IClient> serviceLoaderMock;

    @Test
    public void getInstanceTest() {
        // **** When

        // Prepare the ServiceLoader with a Provider<IClient>
        // 1) Initialize the clientMock that will be returned
        final IClient.ClientTypeEnum clientType = IClient.ClientTypeEnum.PUB;
        final IClient clientMock = Mockito.mock(IClient.class);
        Mockito.when(clientMock.getClientType()).thenReturn(clientType);

        // 2) Create the ServiceLoader's Provider
        final ServiceLoader.Provider<IClient> provider = Mockito.mock(ServiceLoader.Provider.class);
        Mockito.when(provider.get()).thenReturn(clientMock);

        // 3) Mock the Stream with the ServiceLoader
        final Stream<ServiceLoader.Provider<IClient>> clientsStream = Stream.of(provider);

        // 4) Mock the static Service Loader
        try (MockedStatic<ServiceLoader> serviceLoaderFactoryMock = Mockito.mockStatic(ServiceLoader.class)) {
            serviceLoaderFactoryMock.when( () -> ServiceLoader.load(IClient.class)).thenReturn(serviceLoaderMock);
            Mockito.when(serviceLoaderMock.stream()).thenReturn(clientsStream);

            // **** Then
            final IClient clientResult = ClientFactory.getInstance(clientType);

            // **** Verify
            Assertions.assertEquals(clientMock, clientResult);
        }
    }
}
