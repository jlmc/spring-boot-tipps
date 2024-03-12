package io.github.jlmc.cwr.service.domain.clubs.services;

import io.github.jlmc.cwr.service.common.ClubRepresentation;
import io.github.jlmc.cwr.service.common.SeasonTeam;
import io.github.jlmc.cwr.service.domain.clubs.entities.Season;
import io.github.jlmc.cwr.service.domain.clubs.entities.Team;
import io.github.jlmc.cwr.service.domain.clubs.mappers.SeasonTeamMapper;
import io.github.jlmc.cwr.service.domain.clubs.repository.ClubRepository;
import io.github.jlmc.cwr.service.domain.common.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ClubQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClubQueryService.class);

    private final ClubRepository repository;

    private final SeasonTeamMapper seasonTeamMapper;


    public ClubQueryService(ClubRepository repository, SeasonTeamMapper seasonTeamMapper) {
        this.repository = repository;
        this.seasonTeamMapper = seasonTeamMapper;
    }

    public Optional<ClubRepresentation> findClub(Long id) {
        LOGGER.info("Finding the Club with the id {}", id);

        return repository.findById(id)
                .map(entity -> new ClubRepresentation(entity.getId(), entity.getName()));
    }

    public Optional<SeasonTeam> findSeasonTeam(Long clubId, String season) {
        LOGGER.info("Finding club {} Season {} Team", clubId, season);

        Team team = repository.findSeasonTeam(clubId, Season.valueOf(season));

        if (team == null) {
            throw new NotFoundException("No season %s team of the club %s".formatted(season, clubId));
        }

        return Optional.of(seasonTeamMapper.toSeasonTeam(team));
    }


}
