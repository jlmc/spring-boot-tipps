package io.github.jlmc.orders.application.internal.commandservices;

import io.github.jlmc.orders.application.internal.outboundservices.acl.OrderEventProducer;
import io.github.jlmc.orders.domain.model.aggregates.Order;
import io.github.jlmc.orders.domain.model.commands.Item;
import io.github.jlmc.orders.domain.model.commands.UpdateOrderCommand;
import io.github.jlmc.orders.domain.model.entities.Product;
import io.github.jlmc.orders.infrastructure.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateOrderCommandServiceTest {

    @InjectMocks
    UpdateOrderCommandService sut;

    @Mock
    OrderRepository orderRepository;
    @Mock
    ProductResolver productResolver;
    @Mock
    OrderEventProducer orderEventProducer;

    @Test
    void onExecute() {
        String orderId = "1234";
        Instant utc = LocalDate.parse("2022-12-13").atStartOfDay().atZone(ZoneId.of("UTC")).toInstant();
        Order existingOrder = Order.builder().id(orderId).created(utc).build();
        UpdateOrderCommand command =
                new UpdateOrderCommand(orderId, List.of(new Item("1", 1), new Item("2", 3)));


        when(orderRepository.findById(eq(orderId)))
                .thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer((Answer<Order>) invocationOnMock -> invocationOnMock.getArgument(0));

        when(productResolver.productOf(eq("1")))
                .thenReturn(Product.of("1", "iPhone"));
        when(productResolver.productOf(eq("2")))
                .thenReturn(Product.of("2", "MacBookPro"));
        doNothing()
               .when(orderEventProducer)
               .sendUpdateOrderEvent(any(Order.class));

        Order updateOrder = sut.execute(command);

        verify(orderEventProducer, times(1)).sendUpdateOrderEvent(eq(updateOrder));
        assertNotNull(updateOrder);
        assertEquals(orderId, updateOrder.getId());
        assertEquals(utc, updateOrder.getCreated());
        assertEquals(command.items().size(), updateOrder.getOrderItems().size());
    }
}
