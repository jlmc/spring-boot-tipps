package io.github.jlmc.reactordemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FlexAndMonoTest {

    @Test
    void flux() {
        Flux<String> flux =
                Flux.just("java", "kotlin", "reactive")
                    .concatWith(Flux.error(new IllegalStateException("Invalid for some reason")))
                    .concatWith(Flux.just("-- no more data after the error --"))
                .log()
                ;


        flux.subscribe(System.out::println, this::onError, this::onCompleted);
    }

    private void onCompleted() {
        System.out.println("---> Completed ... on happens if no error happens");
    }

    private void onError(Throwable throwable) {
        System.err.println("ERROR: " + throwable.getMessage());
    }

    @Test
    void testFlux_WithoutError() {
        Flux<String> flux = Flux.just("java", "kotlin", "reactive").log();

        StepVerifier.create(flux)
                    .expectNext("java")
                    .expectNext("kotlin")
                    .expectNext("reactive")
                    .verifyComplete();
    }

    @Test
    void testFlux_WithError() {
        Flux<String> flux = Flux.just("java", "kotlin", "reactive")
                .concatWith(Flux.error(new IllegalStateException("Invalid for some reason")))
                .log();

        StepVerifier.create(flux)
                .expectNext("java")
                .expectNext("kotlin")
                .expectNext("reactive")
                //.expectErrorMessage("Invalid for some reason")
                //.expectError(IllegalStateException.class).verify();
                .verifyError(IllegalStateException.class);
    }

    @Test
    void testFlux_Count() {
        Flux<String> flux = Flux.just("java", "kotlin", "reactive")
                .concatWith(Flux.error(new IllegalStateException("Invalid for some reason")))
                .log();

        StepVerifier.create(flux)
                .expectNextCount(3L)
                //.expectErrorMessage("Invalid for some reason")
                //.expectError(IllegalStateException.class).verify();
                .verifyError(IllegalStateException.class);
    }

    @Test
    void mono() {
        Mono<String> mono = Mono.just("Foo");

        StepVerifier.create(mono.log())
                .expectNext("Foo")
                .verifyComplete();
    }

    @Test
    void mono_WithError() {
        Mono<String> mono = Mono.error(new IllegalArgumentException("bad thing"));

        StepVerifier.create(mono.log())
                .verifyError(IllegalArgumentException.class);
    }


}
