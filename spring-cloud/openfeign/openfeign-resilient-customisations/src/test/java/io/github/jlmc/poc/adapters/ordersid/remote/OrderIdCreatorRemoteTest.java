package io.github.jlmc.poc.adapters.ordersid.remote;

import io.github.jlmc.poc.adapters.ordersid.control.remote.OrderIdCreatorRemote;
import io.github.jlmc.poc.adapters.ordersid.control.remote.OrderIdGeneratorServiceApiClient;
import io.github.jlmc.poc.domain.orders.entities.OrderId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderIdCreatorRemoteTest {

    @InjectMocks
    OrderIdCreatorRemote sut;

    @Mock
    OrderIdGeneratorServiceApiClient orderIdGeneratorServiceApiClient;

    @Test
    void when_generate_id_it_delegates_to_client() {
        // Arrange: Set up the objects and data needed for the test
        OrderId orderId = mock(OrderId.class);
        when(orderIdGeneratorServiceApiClient.generateOrderId(any()))
                .thenReturn(orderId);

        // Act: Perform the action (calling the method you want to test)
        OrderId result = sut.generateOrderId();

        // Assert: Verify that the result is what you expect
        assertSame(orderId, result);
        verify(orderIdGeneratorServiceApiClient, times(1)).generateOrderId(any());
    }
}