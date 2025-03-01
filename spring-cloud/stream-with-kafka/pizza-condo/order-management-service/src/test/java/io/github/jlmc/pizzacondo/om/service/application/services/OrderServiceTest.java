package io.github.jlmc.pizzacondo.om.service.application.services;

import io.github.jlmc.pizzacondo.om.service.application.port.input.PlaceOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.application.port.output.OrderRepository;
import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                OrderService.class,
        }
)
class OrderServiceTest {

    @Autowired
    OrderService sut;

    @MockitoBean
    OrderRepository orderRepository;

    @Test
    void whenPlaceOrder() {
        Order savedOrder = mock(Order.class);
        when(orderRepository.add(any(Order.class))).thenReturn(savedOrder);

        PlaceOrderUseCase.PlaceOrderCommand command = new PlaceOrderUseCase.PlaceOrderCommand("1", 2, List.of("CHEESE"));

        sut.placeOrder(command);

        verify(orderRepository, times(1)).add(any(Order.class));
    }

}