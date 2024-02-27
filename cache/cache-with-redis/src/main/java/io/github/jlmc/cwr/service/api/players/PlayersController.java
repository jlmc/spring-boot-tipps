package io.github.jlmc.cwr.service.api.players;


import io.github.jlmc.cwr.service.common.PlayerRepresentation;
import io.github.jlmc.cwr.service.domain.players.services.AddMultiplePlayersService;
import io.github.jlmc.cwr.service.domain.players.services.PlayersSearchQueryService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/players")
public class PlayersController {

    private final PlayersSearchQueryService playersSearchQueryService;
    private final AddMultiplePlayersService addMultiplePlayersService;

    public PlayersController(PlayersSearchQueryService playersSearchQueryService,
                             AddMultiplePlayersService addMultiplePlayersService) {
        this.playersSearchQueryService = playersSearchQueryService;
        this.addMultiplePlayersService = addMultiplePlayersService;
    }

    @GetMapping
    public List<PlayerRepresentation> getPlayer() {
        return playersSearchQueryService.search();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addMultiple(@RequestBody @Validated List<PlayerRepresentation> payload) {
        addMultiplePlayersService.execute(payload);
    }

    @GetMapping(path = "/{id:\\d+}")
    public PlayerRepresentation getPlayerById(@PathVariable("id") Long id) {
        return playersSearchQueryService.findById(id);
    }

}
