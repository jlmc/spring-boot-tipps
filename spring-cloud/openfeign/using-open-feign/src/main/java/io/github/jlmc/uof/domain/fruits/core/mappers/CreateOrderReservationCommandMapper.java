package io.github.jlmc.uof.domain.fruits.core.mappers;

import io.github.jlmc.uof.domain.fruits.commands.CreateOrderReservationCommand;
import io.github.jlmc.uof.domain.fruits.ports.outgoing.MarketProductInformationProvider;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreateOrderReservationCommandMapper {

    public MarketProductInformationProvider.OrderReservationRequest toRequest(CreateOrderReservationCommand command);
}
