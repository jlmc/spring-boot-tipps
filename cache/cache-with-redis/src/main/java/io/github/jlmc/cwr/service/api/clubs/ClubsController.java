package io.github.jlmc.cwr.service.api.clubs;

import io.github.jlmc.cwr.service.common.ClubRepresentation;
import io.github.jlmc.cwr.service.domain.clubs.services.ClubQueryService;
import io.github.jlmc.cwr.service.domain.clubs.services.RegisterNewClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/clubs")
public class ClubsController {

    @Autowired
    RegisterNewClubService registerNewClubService;

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
}
