package io.github.jlmc.cwr.service.domain.players.services;

import io.github.jlmc.cwr.service.common.PlayerRepresentation;
import io.github.jlmc.cwr.service.domain.common.NotFoundException;
import io.github.jlmc.cwr.service.domain.players.mappers.PlayersMapper;
import io.github.jlmc.cwr.service.domain.players.repository.PlayersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PlayersSearchQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayersSearchQueryService.class);

    private final PlayersRepository repository;
    private final PlayersMapper playersMapper;

    public PlayersSearchQueryService(PlayersRepository repository, PlayersMapper playersMapper) {
        this.repository = repository;
        this.playersMapper = playersMapper;
    }

    /**
     * A key é muito importante para legibilidade. Desta forma acordamos (sujeito a discussão) em seguir esta convenção:
     * <service_name>:<cache_name>::<nome_metodo>:<identificador>
     */
    @Cacheable(
            cacheNames = "players",
            key = "#root.methodName"
    )
    public List<PlayerRepresentation> search() {
        LOGGER.info("Getting list of all players");

        return repository
                .findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(playersMapper::toRepresentation)
                .toList();
    }

    @Cacheable(
            cacheNames = "players"
            //key = " #root.methodName + ':' + #id"
    )
    public PlayerRepresentation findById(Long id) {
        LOGGER.info("Finding a player with the id {}", id);

        return repository.findById(id)
                .map(playersMapper::toRepresentation)
                .orElseThrow(() -> new NotFoundException("No player found with the id " + id))
                ;
    }
}
