package io.github.jlmc.reactordemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = List.of("Alvaro", "Bruno", "Carlos", "Daniel", "Edgar", "Fabiano", "Gil");

    @Test
    void filterTest() {
        Flux<String> flux =
                Flux.fromIterable(names)
                    .filter(it -> it.length() <= 5)
                    .log();

        StepVerifier.create(flux)
                    .expectNext("Bruno")
                    .expectNext("Edgar")
                    .expectNext("Gil")
                    .verifyComplete();
    }
}
