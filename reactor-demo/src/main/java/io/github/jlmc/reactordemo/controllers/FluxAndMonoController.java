package io.github.jlmc.reactordemo.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> returnOneFlux() {
        return Flux.just(1, 2, 3, 4)
                   .delayElements(Duration.ofSeconds(1L))
                   .log();
    }

    //@GetMapping(value = "/flux-stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @GetMapping(value = "/flux-stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Long> returnOneFluxStream() {

        return Flux.interval(Duration.ofSeconds(1L))
                   .log();

        /*return Flux.just(1, 2, 3, 4)
                   .delayElements(Duration.ofSeconds(1L))
                   .log();*/
    }

    @GetMapping(path = "/mono")
    public Mono<Integer> returnMono() {
        return Mono.just(1).log();
    }
}
