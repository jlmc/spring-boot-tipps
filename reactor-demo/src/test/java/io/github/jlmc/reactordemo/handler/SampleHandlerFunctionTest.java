package io.github.jlmc.reactordemo.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class SampleHandlerFunctionTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void functionalRouteSampleTest() {
        // /functional/flux"
        // /functional/mono"

        Flux<Integer> responseBody = webTestClient.get().uri("/functional/flux")
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
    void functionalRouteSampleMonoTest() {
        Integer expectedValue = 1;

        webTestClient.get()
                     .uri("/functional/mono")
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody(Integer.class)
                     .consumeWith(response ->
                             assertEquals(expectedValue, response.getResponseBody())
                     );


    }
}