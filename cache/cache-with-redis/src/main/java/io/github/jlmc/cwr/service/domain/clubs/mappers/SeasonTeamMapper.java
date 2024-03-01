package io.github.jlmc.cwr.service.domain.clubs.mappers;

import io.github.jlmc.cwr.service.common.PlayerRepresentation;
import io.github.jlmc.cwr.service.common.SeasonTeam;
import io.github.jlmc.cwr.service.domain.clubs.entities.Team;
import io.github.jlmc.cwr.service.domain.players.mappers.PlayersMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class SeasonTeamMapper {

    @Autowired
    private PlayersMapper playersMapper;

    public SeasonTeam toSeasonTeam(Team team) {
        String title = team.getSeason().title();
        List<PlayerRepresentation> players =
                team.getPlayers().stream().map(playersMapper::toRepresentation).toList();

        return new SeasonTeam(title, players);
    }
}
