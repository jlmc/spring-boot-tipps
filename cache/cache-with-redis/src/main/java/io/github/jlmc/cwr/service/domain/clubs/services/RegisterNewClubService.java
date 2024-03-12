package io.github.jlmc.cwr.service.domain.clubs.services;

import io.github.jlmc.cwr.service.common.ClubRepresentation;
import io.github.jlmc.cwr.service.domain.clubs.entities.Club;
import io.github.jlmc.cwr.service.domain.clubs.repository.ClubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterNewClubService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterNewClubService.class);

    private final ClubRepository repository;

    public RegisterNewClubService(ClubRepository repository) {
        this.repository = repository;
    }

    public ClubRepresentation execute(String clubName) {
        LOGGER.info("Register New Club with the name {}", clubName);

        Club club = repository.saveAndFlush(Club.createClub(clubName));

        return new ClubRepresentation(club.getId(), club.getName());
    }
}
