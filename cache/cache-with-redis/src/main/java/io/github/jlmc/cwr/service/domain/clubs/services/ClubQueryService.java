package io.github.jlmc.cwr.service.domain.clubs.services;

import io.github.jlmc.cwr.service.common.ClubRepresentation;
import io.github.jlmc.cwr.service.domain.clubs.repository.ClubRepository;
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


    public ClubQueryService(ClubRepository repository) {
        this.repository = repository;
    }

    public Optional<ClubRepresentation> findClub(Long id) {
        LOGGER.info("Finding the Club with the id {}", id);

        return repository.findById(id)
                .map(entity -> new ClubRepresentation(entity.getId(), entity.getName()));
    }
}
