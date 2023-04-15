package io.github.jlmc.uploadcsv.adviser.boundary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.jlmc.uploadcsv.Resources;
import io.github.jlmc.uploadcsv.adviser.control.ConstraintViolationExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.control.CsvConstraintViolationsExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.control.MethodArgumentNotValidExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.control.WebExchangeBindExceptionHandler;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.jlmc.uploadcsv.Resources.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@WebFluxTest()
@AutoConfigureWebTestClient(timeout = "36000")
@ContextConfiguration(
        classes = {
                ControllerAdviceTest.CustomersController.class,
                ConstraintViolationExceptionHandler.class,
                WebExchangeBindExceptionHandler.class,
                MethodArgumentNotValidExceptionHandler.class,
                CsvConstraintViolationsExceptionHandler.class,
                CsvIllegalDataExceptionHandler.class,
                ApiErrorsConfigurationProperties.class,
                ControllerAdvice.class,
        }
)
class ControllerAdviceTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("""
            when I call a controller with correct query parameters, it should return a ok
            """
    )
    void noErrorTest() {
        webTestClient.get()
                     .uri(uriBuilder ->
                             uriBuilder
                                     .path("/customers")
                                     .queryParam("page", 1)
                                     .queryParam("per_page", 4)
                                     .build()
                     )
                     .exchange()
                     .expectStatus().isOk();
    }

    @Test
    @DisplayName("""
            when I call a controller with one invalid query parameter, it should return an response with error fields
            """
    )
    void oneInvalidParameter() {
        webTestClient.get()
                     .uri(uriBuilder ->
                             uriBuilder
                                     .path("/customers")
                                     .queryParam("page", 1)
                                     .queryParam("per_page", 11)
                                     .build()
                     )
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody()
                     .json(classPathResourceContent("payloads/ControllerAdviceTest/oneInvalidParameter/response.json"), true);
    }

    @Test
    @DisplayName("""
            when I call a controller with one invalid path parameter,
            it should return an response with error fields
            """
    )
    void onePathParameterIsInvalid() {
        webTestClient.get()
                     .uri(uriBuilder ->
                             uriBuilder
                                     .path("/customers/{id}")
                                     .build("invalid-customer-id")
                     )
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody()
                     .json(
                             Resources.classPathResourceContent("payloads/ControllerAdviceTest/onePathParameterIsInvalid/response.json"),
                             true);
    }

    private void requestAndVerify(
            String requestPayload,
            String expectedResponse) {
        webTestClient.post()
                     .uri("/customers")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(requestPayload))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST.value())
                     .expectBody()
                     .json(expectedResponse, true);
    }

    private void requestAndVerify(Resource requestPayload, Resource expectedResponse) {
        webTestClient.post()
                     .uri("/customers")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromResource(requestPayload))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody()
                     .json(resourceContent(expectedResponse), true);
    }

    @Test
    @DisplayName("when a payload contains a violation in nested collection of strings should should return bad request")
    void violationInCollectionOfStrings() {
        Resource request = classPathResource("payloads/ControllerAdviceTest/violationInCollectionOfStrings/request.json");
        Resource response = classPathResource("payloads/ControllerAdviceTest/violationInCollectionOfStrings/response.json");

        requestAndVerify(resourceContent(request), resourceContent(response));
    }

    @Test
    @DisplayName("when the violation is on the root of the request payload")
    void violationOnTheRootOfPayload() {
        Resource resource = classPathResource("payloads/ControllerAdviceTest/violationOnTheRootOfPayload/request.json");
        Resource response = classPathResource("payloads/ControllerAdviceTest/violationOnTheRootOfPayload/response.json");

        requestAndVerify(resource, response);
    }

    @Test
    @DisplayName("when the violation is on a child node object of the root of the request payload")
    void violationOnChildOfRootOfPayload() {
        requestAndVerify(
                classPathResource("payloads/ControllerAdviceTest/violationOnChildOfRootOfPayload/request.json"),
                classPathResource("payloads/ControllerAdviceTest/violationOnChildOfRootOfPayload/response.json"));
    }

    @Test
    @DisplayName("when the violation is on one array nested in the root of the request payload")
    void violationOnArrayElementInRootPayload() {
        requestAndVerify(
                classPathResource("payloads/ControllerAdviceTest/violationOnArrayElementInRootPayload/request.json"),
                classPathResource("payloads/ControllerAdviceTest/violationOnArrayElementInRootPayload/response.json")
        );
    }

    @Validated
    @RestController
    @RequestMapping("/customers")
    static class CustomersController {

        @NotNull
        private static String getCustomer(Object id) {
            return "Customer " + id;
        }

        @GetMapping
        public Mono<List<String>> list(
                @Min(1)
                @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
                @Range(min = 1, max = 10)
                @RequestParam(value = "per_page", defaultValue = "5", required = false) Integer perPage) {
            Stream<String> customers = IntStream.range(1, 51)
                                                .mapToObj(CustomersController::getCustomer)
                                                .skip((long) (page - 1) * perPage)
                                                .limit(perPage);
            return Mono.just(customers.toList());
        }

        @GetMapping("/{customer_id}")
        public Mono<String> getById(@PathVariable("customer_id") @Pattern(regexp = "^\\d+$") String id) {
            return Mono.just(getCustomer(id));
        }

        @PostMapping(consumes = APPLICATION_JSON_VALUE)
        public Mono<CustomerRepresentation> add(
                @RequestBody @Validated CustomerRepresentation payload) {
            System.out.println("Received payload: [ " + payload + " ]");

            payload.setId("generated-id");
            return Mono.just(payload);
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class CustomerRepresentation {
        String id;
        @NotBlank
        String firstName;
        @NotBlank
        String lastName;
        @Valid
        Details details;
        @Valid
        List<@Valid ContactRepresentation> contacts;
    }

    @Data
    static class Details {
        String nif;
        @Min(1990)
        Integer bornYear;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class ContactRepresentation {
        String type;
        @NotBlank
        String value;
        @Size(max = 5)
        List<@Valid @NotBlank String> useTags;
    }
}
