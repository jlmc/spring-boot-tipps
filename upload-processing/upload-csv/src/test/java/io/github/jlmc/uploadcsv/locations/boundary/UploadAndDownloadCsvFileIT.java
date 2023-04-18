package io.github.jlmc.uploadcsv.locations.boundary;

import io.github.jlmc.uploadcsv.BodyFactory;
import io.github.jlmc.uploadcsv.Containers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.jlmc.uploadcsv.Resources.classPathResource;
import static io.github.jlmc.uploadcsv.Resources.classPathResourceContent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureWebTestClient
@org.springframework.boot.test.context.SpringBootTest
class UploadAndDownloadCsvFileIT {

    public static final String ACCOUNT_ID = "1";
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LIST_PARAMETERIZED_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };
    @Container
    static MongoDBContainer mongoDBContainer = Containers.getMongoDBContainer();
    @Autowired
    private WebTestClient webClient;

    @DynamicPropertySource
    static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void uploadFileWithViolations() {
        var resource = classPathResource("payloads/UploadAndDownloadCsvFileIT/invalid-csv/request.csv");

        webClient.post()
                 .uri(uriBuilder -> uriBuilder.path("/locations-bulk/{accountId}")
                                              .build(ACCOUNT_ID))
                 .body(BodyFactory.fileMultipart(resource))
                 .exchange()
                 .expectStatus().isBadRequest()
                 .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                 .expectBody()
                 .json(classPathResourceContent("payloads/UploadAndDownloadCsvFileIT/invalid-csv/response.json"), true);
    }

    @Test
    void happyPath() {
        var resource = classPathResource("payloads/UploadAndDownloadCsvFileIT/happy-flow/request.csv");

        uploadCsv(resource);

        var locationsAsJson = getLocationsAsJson();

        String[] ids = locationsAsJson
                .stream()
                .map(it -> it.get("id"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .toArray(String[]::new);

        downloadCsv(ids);
    }

    private void downloadCsv(String[] ids) {
        @SuppressWarnings("ConfusingArgumentToVarargsMethod")
        String expectedDownloadFile = String.format(classPathResourceContent("payloads/UploadAndDownloadCsvFileIT/happy-flow/response.csv"), ids);

        webClient.get()
                 .uri(uriBuilder -> uriBuilder.path("/locations-bulk/{accountId}")
                                              .build(ACCOUNT_ID))
                 .exchange()
                 .expectHeader().contentType("text/csv")
                 .expectStatus().isOk()
                 .expectBody(String.class)
                 .consumeWith(it -> {
                     String responseBody = it.getResponseBody();
                     assertNotNull(responseBody);
                     List<String> lines = responseBody.lines().toList();
                     assertEquals(4, lines.size());

                     System.out.printf("""
                             ==== CSV ===
                             %s
                             ======
                             %n""", responseBody);

                     assertEquals(expectedDownloadFile, responseBody);
                 });
    }

    private List<Map<String, Object>> getLocationsAsJson() {
        return getExistingLocationsAsJson();
    }

    private List<Map<String, Object>> getExistingLocationsAsJson() {
        return Objects.requireNonNull(webClient.get()
                                               .uri(uriBuilder -> uriBuilder.path("/locations/{accountId}")
                                                                            .build(ACCOUNT_ID))
                                               .exchange()
                                               .expectStatus().isOk()
                                               .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                                               .expectBody(LIST_PARAMETERIZED_TYPE_REFERENCE)
                                               .consumeWith(it -> {
                                                   List<Map<String, Object>> response = it.getResponseBody();

                                                   assertNotNull(response);
                                                   assertEquals(3, response.size());
                                                   assertEquals("Almada", response.get(0).get("name"));
                                                   assertEquals("New Milford", response.get(1).get("name"));
                                                   assertEquals("South Lyon", response.get(2).get("name"));


                                                   var returnedIds = response.stream()
                                                                             .map(entry -> entry.get("id"))
                                                                             .filter(Objects::nonNull)
                                                                             .map(Object::toString)
                                                                             .toList();

                                                   assertEquals(3, returnedIds.size());

                                               })
                                               .returnResult()
                                               .getResponseBody())
                      .stream()
                      .toList();
    }

    private void uploadCsv(Resource resource) {
        webClient.post()
                 .uri(uriBuilder -> uriBuilder.path("/locations-bulk/{accountId}")
                                              .build(ACCOUNT_ID))
                 .body(BodyFactory.fileMultipart(resource))
                 .exchange()
                 .expectStatus().isNoContent();
    }
}
