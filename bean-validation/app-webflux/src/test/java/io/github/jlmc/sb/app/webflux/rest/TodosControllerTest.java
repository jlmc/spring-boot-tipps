package io.github.jlmc.sb.app.webflux.rest;

import io.github.jlmc.sb.validation.ValidationsAutoConfiguration;
import io.github.jlmc.sb.validation.advices.ConstraintViolationExceptionHandler;
import io.github.jlmc.sb.validation.advices.MethodArgumentNotValidExceptionHandler;
import io.github.jlmc.sb.validation.advices.WebExchangeBindExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@WebFluxTest
@ContextConfiguration(
        classes = {
                TodosController.class,
                ControllerExceptionAdvices.class,
                ValidationsAutoConfiguration.class,
                ConstraintViolationExceptionHandler.class,
                MethodArgumentNotValidExceptionHandler.class,
                WebExchangeBindExceptionHandler.class
        }
)
class TodosControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void getPageSuccessful() {
        webTestClient.get()
                     .uri(uriBuilder ->
                             uriBuilder.path("/todos")
                                       .queryParam("page", 2)
                                       .queryParam("page_size", 3)
                                       .build()

                     )
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody()
                     .json("""
                             [
                               {
                                 "id": 4,
                                 "title": "todo-4",
                                 "description": "todo description 4",
                                 "issue_priority": 1
                               },
                               {
                                 "id": 5,
                                 "title": "todo-5",
                                 "description": "todo description 5",
                                 "issue_priority": 1
                               },
                               {
                                 "id": 6,
                                 "title": "todo-6",
                                 "description": "todo description 6",
                                 "issue_priority": 1
                               }
                             ]
                             """
                     );
    }

    @Test
    void getPageBadRequest() {
        webTestClient.get()
                     .uri(uriBuilder ->
                             uriBuilder.path("/todos")
                                       .queryParam("page", -1)
                                       .queryParam("page_size", 51)
                                       .build()

                     )
                     .exchange()
                     .expectStatus()
                     .isBadRequest()
                     .expectBody()
                     .json("""
                             {
                               "http_code": 400,
                               "code": "X400",
                               "message": "Request cannot be processed due to validation errors",
                               "fields": [
                                 {
                                   "name": "page_size",
                                   "description": "The Query parameter 'page_size' must be less than or equal to 50"
                                 },
                                 {
                                   "name": "page",
                                   "description": "The Query parameter 'page' must be greater than 0"
                                 }
                               ]
                             }
                             """
                     );
    }


    @Test
    void getByIdSuccessful() {
        webTestClient.get()
                     .uri(uriBuilder ->
                             uriBuilder.path("/todos")
                                       .path("/{id}")
                                       .build("12345")

                     )
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody()
                     .json("""
                             {
                               "id": 12345,
                               "title": "todo-12345",
                               "description": "todo description 12345",
                               "issue_priority": 1
                             }
                             """);
    }

    @Test
    void getByIdBadRequest() {
        webTestClient.get()
                     .uri(uriBuilder ->
                             uriBuilder.path("/todos")
                                       .path("/{id}")
                                       .build("123456")

                     )
                     .exchange()
                     .expectStatus()
                     .isBadRequest()
                     .expectBody()
                     .json("""
                             {
                               "http_code": 400,
                               "code": "X400",
                               "message": "Request cannot be processed due to validation errors",
                               "fields": [
                                 {
                                       "name" : "id",
                                       "description" : "The Path parameter 'id' must match \\"^\\\\d{1,5}$\\""
                                 }
                               ]
                             }
                             """);
    }

    @Test
    void postSuccessful() {
        byte[] bytes = ClassPathResources.fromFile("payloads/requests/valid-todo.json");

        webTestClient.post()
                     .uri("/todos")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(new String(bytes)))
                     .exchange()
                     .expectStatus()
                     .isCreated()
                     .expectBody()
                     .json("""
                             {
                               "id": 12345,
                               "title": "todo-X",
                               "description": "description-X",
                               "issue_priority": 3
                             }
                             """
                     );
    }

    @Test
    void postBadRequest() {
        byte[] bytes = ClassPathResources.fromFile("payloads/requests/invalid-todo.json");

        webTestClient.post()
                     .uri("/todos")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(new String(bytes)))
                     .exchange()
                     .expectStatus()
                     .isBadRequest()
                     .expectBody()
                     .json("""
                             {
                               "http_code" : 400,
                               "code" : "X400",
                               "message" : "Request cannot be processed due to validation errors",
                               "fields" : [ {
                                 "name" : "title",
                                 "description" : "must not be blank"
                               }, {
                                 "name" : "issue_priority",
                                 "description" : "must be less than or equal to 5"
                               } ]
                             }
                             """
                     );
    }
}
