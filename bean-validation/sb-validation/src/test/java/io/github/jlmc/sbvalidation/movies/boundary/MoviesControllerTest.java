package io.github.jlmc.sbvalidation.movies.boundary;

import io.github.jlmc.sbvalidation.MockClockConfigurations;
import io.github.jlmc.sbvalidation.Resources;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(MockClockConfigurations.class)
class MoviesControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    private ResourceLoader resourceLoader;

    @Nested
    class GetMovies {
        @Test
        @DisplayName("with the right parameter, it return ok")
        void successful() {

            Resource resource = resourceLoader.getResource("classpath:response/movies-ok-response.json");
            String expectedJson = Resources.resourceContent(resource);

            webTestClient.get()
                         .uri(uriBuilder ->
                                 uriBuilder.path(MoviesConstants.MOVIES)
                                           .queryParam("page", 0)
                                           .queryParam("page_size", 50)
                                           .queryParam("director", "any")
                                           .build())
                         .exchange()
                         .expectStatus().isOk()
                         .expectHeader().contentType(MediaType.APPLICATION_JSON)
                         .expectBody()
                         .json(expectedJson);
        }

        @Test
        void getOnMoviesWithInvalidPageSize() {
            Resource resource = resourceLoader.getResource("classpath:response/movies-400-response.json");
            String expectedJson = Resources.resourceContent(resource);

            webTestClient.get()
                         .uri(uriBuilder ->
                                 uriBuilder.path(MoviesConstants.MOVIES)
                                           .queryParam("page_size", 51)
                                           .queryParam("page", -1)
                                           .queryParam("director", "any")
                                           .build())
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectHeader().contentType(MediaType.APPLICATION_JSON)
                         .expectBody()
                         .json(expectedJson);
        }

        @Test
        void getOnMoviesWithInvalidFilters() {
            Resource resource = resourceLoader.getResource("classpath:response/movies-400-response.json");
            String expectedJson = Resources.resourceContent(resource);

            webTestClient.get()
                         .uri(uriBuilder ->
                                 uriBuilder.path(MoviesConstants.MOVIES)
                                           .queryParam("director", "")
                                           .build())
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectHeader().contentType(MediaType.APPLICATION_JSON)
                         .expectBody()
                         .json("""
                                 {
                                   "x": "X",
                                   "timestamp": "2023-01-01T21:30:01Z",
                                   "path": "/movies",
                                   "status": 400,
                                   "error": "Bad request",
                                   "message": "Request cannot be processed due to validation errors",
                                   "fields":
                                   [
                                     {
                                       "field": "director",
                                       "message": "must not be empty"
                                     }
                                   ]
                                 }
                                 """);
        }
    }

    @Nested
    class GetMovieById {
        @Test
        @DisplayName("When GET by ID of non existing Item Then should return NOT_FOUND without item representation in the body")
        void getOnItemNotFound() {
            webTestClient.get().uri(MoviesConstants.MOVIES.concat("/{id}"), "non-existing")
                         .exchange()
                         .expectStatus()
                         .isNotFound();
        }

        @Test
        @DisplayName("When GET by ID of existing Item Then should return NOT_FOUND without item representation in the body")
        void getOnItemOK() {
            webTestClient.get().uri(MoviesConstants.MOVIES.concat("/{id}"), "1")
                         .exchange()
                         .expectStatus()
                         .isOk()
                         .expectBody()
                         .json(
                                 Resources.classPathResourceContent(
                                         "response/movies-1-ok-response.json"
                                 )
                         );
        }
    }

    @Nested
    class PostMovie {


        @Test
        void createBook() {
            String requestPayload = Resources.classPathResourceContent("request/create-movie-request-invalid-payload.json");

            webTestClient.post()
                         .uri(MoviesConstants.MOVIES)
                         .contentType(MediaType.APPLICATION_JSON)
                         .bodyValue(
                                 requestPayload
                         )
                         //.body(Mono.just(item), Item.class)
                         .exchange()
                         .expectStatus().isBadRequest()
                         .expectBody()
                         .json("""
                                 {}
                                 """);
//                         .jsonPath("$.id").isNotEmpty()
//                         .jsonPath("$.description").isEqualTo(item.getDescription())
//                         .jsonPath("$.price").isEqualTo(item.getPrice());
        }
    }
}
