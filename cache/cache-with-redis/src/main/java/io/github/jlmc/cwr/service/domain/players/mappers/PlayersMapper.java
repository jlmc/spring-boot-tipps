package io.github.jlmc.cwr.service.domain.players.mappers;

import io.github.jlmc.cwr.service.common.PlayerRepresentation;
import io.github.jlmc.cwr.service.domain.players.entities.Player;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlayersMapper {

    PlayerRepresentation toRepresentation(Player player);
}
