package io.github.jlmc.poc.adapters.orders;

import io.github.jlmc.poc.domain.orders.entities.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;


class NoOpOrderRepositoryTest {

    @Test
    void when_save_order() {
        NoOpOrderRepository noOpOrderRepository = new NoOpOrderRepository();
        Order unsavedOrder = Mockito.mock(Order.class);

        var sabedOrder = noOpOrderRepository.save(unsavedOrder);

        assertEquals(unsavedOrder, sabedOrder);
    }

}