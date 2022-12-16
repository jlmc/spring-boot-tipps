package io.github.jlmc.orders.domain.model.commands;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateOrderCommandTest {

    @Test
    @DisplayName("when no empty items are provider, it throw a IllegalArgumentException")
    void createCreateOrderCommandWithNullItems() {
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class,
                        () -> new CreateOrderCommand(List.of()));

        assertEquals("The items cant be null or empty", illegalArgumentException.getMessage());
    }

    @Test
    @DisplayName("when empty items are provider, it throw a IllegalArgumentException")
    void createCreateOrderCommandWithEmptyItems() {
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class,
                        () -> new CreateOrderCommand(List.of()));

        assertEquals("The items cant be null or empty", illegalArgumentException.getMessage());
    }

}
