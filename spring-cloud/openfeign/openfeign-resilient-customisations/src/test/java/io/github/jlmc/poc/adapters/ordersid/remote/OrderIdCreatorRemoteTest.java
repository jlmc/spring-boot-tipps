package io.github.jlmc.poc.adapters.ordersid.remote;

import io.github.jlmc.poc.domain.orders.entities.OrderId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(orderIdGeneratorServiceApiClient.generateOrderId())
                .thenReturn(orderId);

        // Act: Perform the action (calling the method you want to test)
        OrderId result = sut.generateOrderId();

        // Assert: Verify that the result is what you expect
        assertSame(orderId, result);
        verify(orderIdGeneratorServiceApiClient, times(1)).generateOrderId();
    }
}