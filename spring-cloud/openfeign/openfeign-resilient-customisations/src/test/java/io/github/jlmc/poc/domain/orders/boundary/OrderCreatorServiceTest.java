package io.github.jlmc.poc.domain.orders.boundary;

import io.github.jlmc.poc.configurations.clock.FixedClockConfiguration;
import io.github.jlmc.poc.domain.orders.commands.CreateOrderCommand;
import io.github.jlmc.poc.domain.orders.commands.CreateOrderCommand.Item;
import io.github.jlmc.poc.domain.orders.entities.Order;
import io.github.jlmc.poc.domain.orders.entities.OrderId;
import io.github.jlmc.poc.domain.orders.entities.OrderItem;
import io.github.jlmc.poc.domain.orders.entities.Product;
import io.github.jlmc.poc.domain.orders.ports.outgoing.OrderIdCreator;
import io.github.jlmc.poc.domain.orders.ports.outgoing.OrderRepository;
import io.github.jlmc.poc.domain.orders.ports.outgoing.ProductProvider;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = OrderCreatorService.class)

@Import({ValidationAutoConfiguration.class, FixedClockConfiguration.class})
class OrderCreatorServiceTest {

    @Autowired
    OrderCreatorService sut;

    @MockBean
    ProductProvider productProvider;

    @MockBean
    OrderIdCreator orderIdCreator;

    @MockBean
    OrderRepository orderRepository;

    @Captor
    ArgumentCaptor<Order> captor;

    @Test
    void when_create_a_new_order_with_successfully() {
        String productIdApples = "1";
        String productIdStrawberries = "2";

        // prepare scenario
        Product apples = new Product(Integer.valueOf(productIdApples), "Apples 1Kg", new BigDecimal("5"), "Apples - Pink Lady 1Kg");
        when(productProvider.getProduct(Integer.valueOf(productIdApples))).thenReturn(apples);
        Product strawberries = new Product(Integer.valueOf(productIdStrawberries), "Strawberries 2Kg", new BigDecimal("10"), "Strawberries 10€/1Kg");
        when(productProvider.getProduct(Integer.valueOf(productIdStrawberries))).thenReturn(strawberries);
        OrderId orderId = new OrderId("mocked-order-id");
        Mockito.when(orderIdCreator.generateOrderId()).thenReturn(orderId);

        // execution
        CreateOrderCommand command = new CreateOrderCommand(List.of(new Item(productIdApples, 2), new Item(productIdStrawberries, 2)));
        OrderId result = sut.createOrder(command);

        // verification
        assertEquals(orderId, result);
        verify(orderRepository, times(1)).save(captor.capture());
        Order savedOrder = captor.getValue();
        assertEquals(orderId, savedOrder.getId());
        assertEquals(new BigDecimal("30"), savedOrder.getTotalPrice());
        assertEquals(2, savedOrder.getItems().size());
        assertThat(savedOrder.getItems())
                .containsExactlyInAnyOrderElementsOf(Set.of(OrderItem.orderItem(2, apples), OrderItem.orderItem(2, strawberries)));
    }

    @Test
    void when_create_a_new_order_with_command_without_items() {
        // prepare scenario
        // execution
        CreateOrderCommand command = new CreateOrderCommand(List.of());

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> sut.createOrder(command));
        assertEquals("createOrder.createOrderCommand.items: must not be empty", ex.getMessage());
    }

    @Test
    void when_create_a_new_order_with_command_with_invalid_product_id() {
        // prepare scenario
        CreateOrderCommand command = new CreateOrderCommand(
                List.of(new Item("1", 2), new Item("invalid-id", 2))
        );


        // execution
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> sut.createOrder(command));
        assertEquals("createOrder.createOrderCommand.items[1].productId: must match \"^\\d+$\"", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void when_create_a_new_order_with_command_with_invalid_quantity_id(int quantity) {
        // prepare scenario
        CreateOrderCommand command = new CreateOrderCommand(
                List.of(new Item("1", 2), new Item("2", quantity))
        );


        // execution
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> sut.createOrder(command));
        assertEquals("createOrder.createOrderCommand.items[1].quantity: must be greater than 0", ex.getMessage());
    }

}