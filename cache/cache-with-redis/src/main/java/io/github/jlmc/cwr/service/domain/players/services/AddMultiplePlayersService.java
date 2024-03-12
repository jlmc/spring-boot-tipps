package io.github.jlmc.cwr.service.domain.players.services;

import io.github.jlmc.cwr.service.common.PlayerRepresentation;
import io.github.jlmc.cwr.service.domain.players.entities.Player;
import io.github.jlmc.cwr.service.domain.players.repository.PlayersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static io.github.jlmc.cwr.service.domain.players.entities.Player.newPlayerWith;

@Service
@Transactional(readOnly = true)
public class AddMultiplePlayersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddMultiplePlayersService.class);

    private final PlayersRepository repository;

    public AddMultiplePlayersService(PlayersRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(Collection<PlayerRepresentation> inputs) {
        LOGGER.info("Adding players in bash mode... trying to add {} new players records", inputs.size());


        for (PlayerRepresentation input : inputs) {
            LOGGER.debug("Processing the input {}", input);
            Player newPlayer = newPlayerWith(input.name(), input.birthdate(), input.countryCode());

            repository.save(newPlayer);
        }
        // execute the persistence context pending actions
        repository.flush();

        LOGGER.info("Added Multiple players records successful");
    }
}
