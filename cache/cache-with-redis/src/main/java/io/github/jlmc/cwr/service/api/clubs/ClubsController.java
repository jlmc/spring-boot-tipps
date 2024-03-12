package io.github.jlmc.cwr.service.api.clubs;

import io.github.jlmc.cwr.service.api.clubs.requests.AddClubSeasonTeamRequest;
import io.github.jlmc.cwr.service.api.clubs.requests.CreateClubRequest;
import io.github.jlmc.cwr.service.common.ClubRepresentation;
import io.github.jlmc.cwr.service.common.SeasonTeam;
import io.github.jlmc.cwr.service.domain.clubs.commands.DefineClubSeasonCommand;
import io.github.jlmc.cwr.service.domain.clubs.services.ClubQueryService;
import io.github.jlmc.cwr.service.domain.clubs.services.DefineClubSeasonCommandService;
import io.github.jlmc.cwr.service.domain.clubs.services.RegisterNewClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/clubs")
public class ClubsController {

    @Autowired
    RegisterNewClubService registerNewClubService;

    @Autowired
    DefineClubSeasonCommandService defineClubSeasonCommandService;

    @Autowired
    ClubQueryService clubQueryService;

    @PostMapping
    public ResponseEntity<ClubRepresentation> createClub(@Validated @RequestBody CreateClubRequest payload) {
        ClubRepresentation result = registerNewClubService.execute(payload.name());

        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(result.id())
                        .toUri();

        return ResponseEntity.created(uri).body(result);
    }

    @GetMapping(path = "/{id:\\d+}")
    public ResponseEntity<ClubRepresentation> getClubById(@PathVariable("id") Long id ) {
        return clubQueryService.findClub(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(path = "/{id:\\d+}/teams")
    public void addSeasonTeam(@PathVariable("id") Long id, @Validated @RequestBody AddClubSeasonTeamRequest payload) {
        DefineClubSeasonCommand commad = new DefineClubSeasonCommand(id, payload.season(), payload.players());
        defineClubSeasonCommandService.execute(commad);
    }

    @GetMapping(path = "/{id:\\d+}/teams/{season}")
    public ResponseEntity<SeasonTeam> getClubSeasonTeam(@PathVariable("id") Long id, @PathVariable("season") String season) {
        Optional<SeasonTeam> seasonTeam = clubQueryService.findSeasonTeam(id, season);
        return seasonTeam.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
