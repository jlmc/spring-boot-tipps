package io.github.jlmc.cwr.service.domain.clubs.services;

import io.github.jlmc.cwr.service.domain.clubs.commands.DefineClubSeasonCommand;
import io.github.jlmc.cwr.service.domain.clubs.entities.Season;
import io.github.jlmc.cwr.service.domain.clubs.entities.Team;
import io.github.jlmc.cwr.service.domain.clubs.repository.ClubRepository;
import io.github.jlmc.cwr.service.domain.common.NotFoundException;
import io.github.jlmc.cwr.service.domain.players.entities.Player;
import io.github.jlmc.cwr.service.domain.players.repository.PlayersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefineClubSeasonCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefineClubSeasonCommandService.class);

    private final ClubRepository clubRepository;
    private final PlayersRepository playersRepository;

    public DefineClubSeasonCommandService(ClubRepository clubRepository, PlayersRepository playersRepository) {
        this.clubRepository = clubRepository;
        this.playersRepository = playersRepository;
    }

    public void execute(DefineClubSeasonCommand command) {
        LOGGER.info("Defining the team of the club {} in the season {}", command.clubId(), command.season());

        var club =
                clubRepository.findById(command.clubId())
                        .orElseThrow(() -> new NotFoundException("Not found any club with the id " + command.clubId()));

        Set<Player> players =
                command.playerIds()
                        .stream()
                        .map(playersRepository::getReferenceById)
                        .collect(Collectors.toSet());

        Season season = Season.valueOf(command.season());

        if (club.containsAnySeason(season)) {
            club.removeTeamAtSeason(season);
            clubRepository.flush();
        }

        Team team = Team.createTeam(season);
        players.forEach(team::addPlayer);

        club.addTeam(team);

        clubRepository.flush();
    }
}
