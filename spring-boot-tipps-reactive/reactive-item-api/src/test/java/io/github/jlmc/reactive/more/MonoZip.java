package io.github.jlmc.reactive.more;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.function.Function;

public class MonoZip {

    @Test
    void zipping() {

        final Mono<Person> dukesMono = Mono.just(new Person("123", "Dukes"));

        final Flux<Account> accountFlux = Flux.fromIterable(List.of(new Account("991", "AA"), new Account("992", "BBB")));
        var result =
                dukesMono.zipWith(accountFlux.collectList())
                         .flatMap((Function<Tuple2<Person, List<Account>>, Mono<Dto>>) objects -> {
                             final Dto data = Dto.from(objects.getT1(), objects.getT2());
                             return Mono.just(data);
                         });

        StepVerifier.create(result.log())
                    .expectSubscription()
                    .expectNextCount(1L)
                    .verifyComplete();
    }




}
