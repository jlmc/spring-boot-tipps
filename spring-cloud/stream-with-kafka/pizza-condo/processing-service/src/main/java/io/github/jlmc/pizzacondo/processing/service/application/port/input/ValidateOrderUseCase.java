package io.github.jlmc.pizzacondo.processing.service.application.port.input;

import java.util.List;

public interface ValidateOrderUseCase {

    boolean canBeSatisfied(ValidateOrderCommand command);

    record ValidateOrderCommand(String orderId,
                                int size,
                                List<String> toppings) {
    }
}
