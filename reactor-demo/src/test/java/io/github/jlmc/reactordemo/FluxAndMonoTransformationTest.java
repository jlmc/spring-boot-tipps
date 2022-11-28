package io.github.jlmc.reactordemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.List;

public class FluxAndMonoTransformationTest {

    List<String> names = List.of("Alvaro", "Bruno", "Carlos", "Daniel");

    @Test
    void transformUsingMap() {
        Flux<String> flux =
                Flux.fromIterable(names)
                    .log()
                    .map(String::toUpperCase)
                    .log();

        StepVerifier.create(flux)
                    .expectNext("ALVARO", "BRUNO", "CARLOS", "DANIEL")
                    .verifyComplete();
    }

    @Test
    void transformUsingFlatMap() {
        List<String> letters = List.of("A", "B", "C", "D");
        Flux<String> flux =
                Flux.fromIterable(letters)
                    .flatMap(it -> Flux.fromIterable(convertToList(it)))
                    .log();

        StepVerifier.create(flux)
                    .expectNext("A", "New Value")
                    .expectNext("B", "New Value")
                    .expectNext("C", "New Value")
                    .expectNext("D", "New Value")
                    .verifyComplete();
    }

    @Test
    void transformUsingFlatMapParallel() {
        List<String> letters = List.of("A", "B", "C", "D");
        Flux<String> flux =
                Flux.fromIterable(letters)
                    .window(2)

                    .flatMap(it -> {
                        return it.map(this::convertToList)
                                 .subscribeOn(Schedulers.parallel())
                                 .flatMap(Flux::fromIterable);
                    })
                    .log();

        StepVerifier.create(flux)
                    .expectNext("A", "New Value")
                    .expectNext("B", "New Value")
                    .expectNext("C", "New Value")
                    .expectNext("D", "New Value")
                    .verifyComplete();
    }


    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1_000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return List.of(s, "New Value");
    }
}
