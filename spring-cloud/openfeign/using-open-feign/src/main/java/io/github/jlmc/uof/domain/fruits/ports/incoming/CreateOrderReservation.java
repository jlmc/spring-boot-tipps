package io.github.jlmc.uof.domain.fruits.ports.incoming;

import io.github.jlmc.uof.domain.fruits.commands.CreateOrderReservationCommand;
import io.github.jlmc.uof.domain.fruits.entities.ReservationId;

public interface CreateOrderReservation {

    ReservationId create(CreateOrderReservationCommand command);
}
