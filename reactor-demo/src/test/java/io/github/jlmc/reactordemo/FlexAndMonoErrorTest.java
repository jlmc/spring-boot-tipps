package io.github.jlmc.reactordemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FlexAndMonoErrorTest {

    @Test
    void flexErrorHandling_onErrorResume() {
        Flux<String> flex =
                Flux.just("A", "B", "C")
                    .concatWith(Flux.error(new IllegalStateException("Error Foo")))
                    .concatWith(Flux.just("Never Done"))
                    .onErrorResume(throwable -> {
                        System.out.println("Error happens: " + throwable);
                        return Flux.just("Default Value");
                    });

        StepVerifier.create(flex.log())
                    .expectSubscription()
                    .expectNext("A", "B", "C")
                    .expectNext("Default Value")
                    //.expectNext("Never Done")
                    //.expectError(IllegalStateException.class)
                    .verifyComplete();
    }

    @Test
    void flexErrorHandling_onErrorReturnDefaultValue() {
        Flux<String> flex =
                Flux.just("A", "B", "C")
                    .concatWith(Flux.error(new IllegalStateException("Error Foo")))
                    .concatWith(Flux.just("Never Done"))
                    .onErrorReturn("DEFAULT");

        StepVerifier.create(flex.log())
                    .expectSubscription()
                    .expectNext("A", "B", "C")
                    .expectNext("DEFAULT")
                    //.expectNext("Never Done")
                    //.expectError(IllegalStateException.class)
                    .verifyComplete();
    }
}
