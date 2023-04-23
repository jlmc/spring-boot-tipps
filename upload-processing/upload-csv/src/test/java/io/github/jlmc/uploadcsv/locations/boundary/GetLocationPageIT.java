package io.github.jlmc.uploadcsv.locations.boundary;

import com.mongodb.client.result.DeleteResult;
import io.github.jlmc.uploadcsv.Containers;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static io.github.jlmc.uploadcsv.ObjectFactory.location;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureWebTestClient(timeout = "PT1M")
@org.springframework.boot.test.context.SpringBootTest
class GetLocationPageIT {

    public static final String ACCOUNT_ID = "1";

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_PARAMETERIZED_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };

    @Container
    static MongoDBContainer mongoDBContainer = Containers.getMongoDBContainer();
    @Autowired
    private WebTestClient webClient;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @DynamicPropertySource
    static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void uploadFileWithViolations() {
        Mono<DeleteResult> removeAll =
                reactiveMongoTemplate.remove(new Query(), Location.class);
        int totalOfElements = 55;
        int pageSize = 10;
        int page = 0;


        var populateLocations =
                reactiveMongoTemplate.insertAll(
                        IntStream.range(0, totalOfElements)
                                 .mapToObj(it -> location(null, ACCOUNT_ID))
                                 .toList());
        removeAll.thenMany(populateLocations)
                 .collectList()
                 .block(Duration.ofSeconds(10));


        webClient.get()
                 .uri(uriBuilder -> uriBuilder.path("/locations/{accountId}/page")
                                              .queryParam("page", page)
                                              .queryParam("size", pageSize)
                                              .build(ACCOUNT_ID))
                 .exchange()
                 .expectStatus().isOk()
                 .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                 .expectBody(MAP_PARAMETERIZED_TYPE_REFERENCE)
                 .consumeWith(pageMap -> {
                     Map<String, Object> responseBody = pageMap.getResponseBody();

                     assertEquals(6, responseBody.get("totalPages"));
                     assertEquals(totalOfElements, responseBody.get("totalElements"));
                     assertEquals(true, responseBody.get("first"));
                     assertEquals(false, responseBody.get("last"));
                     assertEquals(pageSize, responseBody.get("size"));
                     assertEquals(pageSize, responseBody.get("size"));
                     assertEquals(page, responseBody.get("number"));
                     assertNotNull(responseBody.get("content"));
                     @SuppressWarnings("unchecked")
                     List<Map<String, Object>> content = (List<Map<String, Object>>) responseBody.get("content");
                     assertEquals(pageSize, content.size());
                 });
    }


}
