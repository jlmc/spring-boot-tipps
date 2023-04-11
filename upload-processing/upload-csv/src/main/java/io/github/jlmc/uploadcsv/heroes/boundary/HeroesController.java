package io.github.jlmc.uploadcsv.heroes.boundary;

import io.github.jlmc.uploadcsv.heroes.control.HeroRepository;
import io.github.jlmc.uploadcsv.heroes.entity.Hero;
import io.github.jlmc.uploadcsv.heroes.entity.HeroRequestRepresentation;
import io.github.jlmc.uploadcsv.heroes.entity.HeroResponseRepresentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "Heroes service", description = "the heroes API with description tag annotation")


@Validated
@RestController

@RequestMapping(
        path = {"/heroes"},
        produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class HeroesController {

    public final HeroRepository heroRepository;

    public HeroesController(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    @Operation(summary = "Get all the heroes")
    @GetMapping
    public Mono<List<HeroResponseRepresentation>> getHeroes() {
        return heroRepository.findAll()
                             .map(it -> new HeroResponseRepresentation(it.getId(), it.getName(), it.getNickName(), it.getCreatedDate()))
                             .collectList();
    }

    @PostMapping
    public Mono<HeroResponseRepresentation> createOneHero(@RequestBody HeroRequestRepresentation payload) {
        return Mono.just(payload).map(it -> Hero.createHero(it.name(), it.nickName()))
                   .flatMap(heroRepository::save)
                   .map(it -> new HeroResponseRepresentation(it.getId(), it.getName(), it.getNickName(), it.getCreatedDate()));
    }
}
