package io.github.jlmc.reactordemo.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

// For junit 4
//@WebFluxTest
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = { SpringTestConfiguration.class })

@WebFluxTest
@ExtendWith(SpringExtension.class)
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux_approach1() {

        Flux<Integer> responseBody =
                webTestClient.get()
                             .uri("/flux")
                             .accept(MediaType.APPLICATION_JSON)
                             .exchange()
                             .expectStatus().isOk()
                             .returnResult(Integer.class)
                             .getResponseBody();

        StepVerifier.create(responseBody)
                    .expectSubscription()
                    .expectNext(1)
                    .expectNext(2)
                    .expectNext(3)
                    .expectNext(4)
                    .verifyComplete();
    }

    @Test
    void flux_approach2() {

        webTestClient.get()
                     .uri("/flux")
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBodyList(Integer.class)
                     .hasSize(4);
    }

    @Test
    void flux_approach3() {

        List<Integer> expectedValues = List.of(1, 2, 3, 4);

        EntityExchangeResult<List<Integer>> exchangeResult =
                webTestClient.get()
                             .uri("/flux")
                             .accept(MediaType.APPLICATION_JSON)
                             .exchange()
                             .expectStatus().isOk()
                             .expectBodyList(Integer.class)
                             .returnResult();

        Assertions.assertEquals(expectedValues, exchangeResult.getResponseBody());
    }

    @Test
    void flux_approach4() {

        List<Integer> expectedValues = List.of(1, 2, 3, 4);

        webTestClient.get()
                     .uri("/flux")
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBodyList(Integer.class)
                     .consumeWith(response -> {
                         Assertions.assertEquals(expectedValues, response.getResponseBody());
                     });
    }

    @Test
    void fluxstream() {

        // Test case implementation for infinity stream flux

        Flux<Long> responseBody =
                webTestClient.get()
                             .uri("/flux-stream")
                             //.accept(MediaType.APPLICATION_STREAM_JSON)
                             .accept(MediaType.APPLICATION_NDJSON)
                             .exchange()
                             .expectStatus().isOk()
                             .returnResult(Long.class)
                             .getResponseBody();

        StepVerifier.create(responseBody)
                    .expectSubscription()
                    .expectNext(0L)
                    .expectNext(1L)
                    .expectNext(2L)
                    .thenCancel()
                    .verify();
    }

    @Test
    void mono() {

        Integer expectedValue = 1;

        webTestClient.get().uri("/mono")
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody(Integer.class)
                     .consumeWith(response -> {
                         Assertions.assertEquals(expectedValue, response.getResponseBody());
                     });
    }
}