package io.github.jlmc.uof.domain.fruits.core;

import io.github.jlmc.uof.domain.fruits.commands.CreateOrderReservationCommand;
import io.github.jlmc.uof.domain.fruits.core.mappers.CreateOrderReservationCommandMapper;
import io.github.jlmc.uof.domain.fruits.entities.ReservationId;
import io.github.jlmc.uof.domain.fruits.ports.incoming.CreateOrderReservation;
import io.github.jlmc.uof.domain.fruits.ports.outgoing.MarketProductInformationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderReservationService implements CreateOrderReservation {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOrderReservationService.class);

    private final MarketProductInformationProvider marketProductInformationProvider;
    private final CreateOrderReservationCommandMapper mapper;

    public CreateOrderReservationService(MarketProductInformationProvider marketProductInformationProvider,
                                         CreateOrderReservationCommandMapper mapper) {
        this.marketProductInformationProvider = marketProductInformationProvider;
        this.mapper = mapper;
    }

    @Override
    public ReservationId create(CreateOrderReservationCommand command) {
        LOGGER.info("Creating a new Order Reservation: {}", command);

        MarketProductInformationProvider.OrderReservationRequest request = mapper.toRequest(command);

        LOGGER.info("Mapped Order Reservation Request: {}", request);

        String orderReservationId = marketProductInformationProvider.createOrderReservation(request);

        LOGGER.info("The Order Reservation: {}, has been created successful with the id: {}", command, orderReservationId);

        return new ReservationId(orderReservationId);
    }
}
