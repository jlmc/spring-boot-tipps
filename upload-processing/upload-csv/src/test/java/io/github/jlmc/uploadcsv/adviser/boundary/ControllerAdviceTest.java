package io.github.jlmc.uploadcsv.adviser.boundary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
import org.springframework.core.io.ClassPathResource;
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
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    private static String readResource(Resource classPathResource) throws IOException {
        return Files.readString(classPathResource.getFile().toPath());
    }

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
    void oneInvalidParameter() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("payloads/ControllerAdviceTest/oneInvalidParameter/response.json");
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
                     .json(readResource(classPathResource), true);
    }

    @Test
    @DisplayName("""
            when I call a controller with one invalid path parameter,
            it should return an response with error fields
            """
    )
    void onePathParameterIsInvalid() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("payloads/ControllerAdviceTest/onePathParameterIsInvalid/response.json");
        webTestClient.get()
                     .uri(uriBuilder ->
                             uriBuilder
                                     .path("/customers/{id}")
                                     .build("invalid-customer-id")
                     )
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody()
                     .json(readResource(classPathResource), true);
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

    private void requestAndVerify(Resource requestPayload, Resource expectedResponse) throws IOException {
        webTestClient.post()
                     .uri("/customers")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromResource(requestPayload))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody()
                     .json(readResource(expectedResponse), true);
    }

    @Test
    @DisplayName("when a payload contains a violation in nested collection of strings should should return bad request")
    void violationInCollectionOfStrings() throws IOException {
        Resource request = new ClassPathResource("payloads/ControllerAdviceTest/violationInCollectionOfStrings/request.json");
        Resource response = new ClassPathResource("payloads/ControllerAdviceTest/violationInCollectionOfStrings/response.json");

        requestAndVerify(readResource(request), readResource(response));
    }

    @Test
    @DisplayName("when the violation is on the root of the request payload")
    void violationOnTheRootOfPayload() throws IOException {
        Resource resource = new ClassPathResource("payloads/ControllerAdviceTest/violationOnTheRootOfPayload/request.json");
        Resource response = new ClassPathResource("payloads/ControllerAdviceTest/violationOnTheRootOfPayload/response.json");

        requestAndVerify(resource, response);
    }

    @Test
    @DisplayName("when the violation is on a child node object of the root of the request payload")
    void violationOnChildOfRootOfPayload() throws IOException {
        requestAndVerify(
                new ClassPathResource("payloads/ControllerAdviceTest/violationOnChildOfRootOfPayload/request.json"),
                new ClassPathResource("payloads/ControllerAdviceTest/violationOnChildOfRootOfPayload/response.json"));
    }

    @Test
    @DisplayName("when the violation is on one array nested in the root of the request payload")
    void violationOnArrayElementInRootPayload() throws IOException {
        requestAndVerify(
                new ClassPathResource("payloads/ControllerAdviceTest/violationOnArrayElementInRootPayload/request.json"),
                new ClassPathResource("payloads/ControllerAdviceTest/violationOnArrayElementInRootPayload/response.json")
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
