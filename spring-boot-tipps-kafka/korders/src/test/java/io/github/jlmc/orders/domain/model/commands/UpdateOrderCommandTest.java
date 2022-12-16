package io.github.jlmc.orders.domain.model.commands;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateOrderCommandTest {

    @Test
    @DisplayName("when no order id is provider, it throw a IllegalArgumentException")
    void createUpdateOrderCommandWithoutOrderId() {
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class,
                () -> new UpdateOrderCommand(null, List.of(new Item("product 12", 1))));

        assertEquals("The order id can't be blank", illegalArgumentException.getMessage());
    }

    @Test
    @DisplayName("when no order id is blank, it throw a IllegalArgumentException")
    void createUpdateOrderCommandWithoutBlankOrderId() {
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class,
                        () -> new UpdateOrderCommand(" ", List.of(new Item("product 12", 1))));

        assertEquals("The order id can't be blank", illegalArgumentException.getMessage());
    }
}
