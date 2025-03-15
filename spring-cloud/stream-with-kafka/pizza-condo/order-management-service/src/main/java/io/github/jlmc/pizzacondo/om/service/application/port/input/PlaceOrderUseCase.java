package io.github.jlmc.pizzacondo.om.service.application.port.input;

import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface PlaceOrderUseCase {

    Order placeOrder(PlaceOrderCommand command);

    record PlaceOrderCommand(@NotBlank String customerId,
                             @Positive int size,
                             @Size(min = 1) List<String> toppings) {
    }
}
