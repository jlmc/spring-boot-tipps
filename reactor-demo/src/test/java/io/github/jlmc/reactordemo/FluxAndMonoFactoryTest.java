package io.github.jlmc.reactordemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = List.of("Alvaro", "Bruno", "Carlos", "Daniel");

    @Test
    void fluxUsingIterable() {
        Flux<String> flux =
                Flux.fromIterable(names)
                    .log();

        StepVerifier.create(flux)
                    .expectNext("Alvaro", "Bruno", "Carlos", "Daniel")
                    .verifyComplete();
    }

    @Test
    void fluxUsingArray() {
        String[] array = names.toArray(new String[0]);

        Flux<String> flux =
                Flux.fromArray(array)
                    .log();

        StepVerifier.create(flux)
                    .expectNext(array)
                    .verifyComplete();
    }

    @Test
    void fluxUsingStream() {
        Flux<String> flux =
                Flux.fromStream(names.stream())
                    .log();

        StepVerifier.create(flux)
                    .expectNext(names.toArray(new String[0]))
                    // The stream is a lazy execution, you need to execute the verify complete
                    .verifyComplete();
    }

    @Test
    void monoUsingJustOrEmpty() {
        Mono<String> objectMono = Mono.justOrEmpty(null); // Mono.Empty();

        StepVerifier.create(objectMono.log())
                    .verifyComplete();
    }

    @Test
    void monoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "He";
        Mono<String> objectMono = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(objectMono.log())
                    .expectNext("He")
                    .verifyComplete();
    }

    @Test
    void fluxUsingRange() {
        Flux<Integer> flux = Flux.range(1, 3);

        StepVerifier.create(flux)
                    .expectNext(1, 2, 3)
                    .verifyComplete();
    }
}
