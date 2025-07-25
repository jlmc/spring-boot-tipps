package io.github.jlmc.reactive.api;

import io.github.jlmc.reactive.ItemConstants;
import io.github.jlmc.reactive.domain.model.Item;
import io.github.jlmc.reactive.domain.repository.ItemReactiveRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("mongodb-container-test")
class ItemControllerTest {


    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:4.4.4")
                    .withReuse(true) // optional: reuse container across test runs
                    .withCommand("--replSet", "rs0");  // enable replica set

    private List<Item> existemItems;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    public static final String DEFAULT_ID = "1234";
    private static final int N_ITEMS = 4;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository repository;


    @BeforeEach
    void setUp() {
        this.existemItems = repository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(repository::save)
                .thenMany(repository.findAll())
                .collectList()
                .block();
    }

    private List<Item> data() {
        return Stream.iterate(1, i -> i <= N_ITEMS, i -> i + 1)
                .map(i -> {
                    String id = i == 1 ? DEFAULT_ID : null;
                    return new Item(id, "Item " + i, 1500.0D + i);
                })
                .collect(toList());
    }

    @Test
    void getAllItemApproach1() {
        List<Item> responseBody = webTestClient.get()
                .uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(N_ITEMS)
                .returnResult()
                .getResponseBody();

        System.out.println(responseBody);
    }

    @Test
    void getAllItemApproach2() {
        webTestClient.get()
                .uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(N_ITEMS)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    Assertions.assertNotNull(items);
                    List<Item> itemsWithoutId =
                            items.stream()
                                    .filter(it -> it.getId() == null)
                                    .collect(toList());
                    Assertions.assertTrue(itemsWithoutId.isEmpty());
                });
    }

    @Test
    void getAllItemApproach3() {
        Flux<Item> responseBody =
                webTestClient.get()
                        .uri(ItemConstants.ITEM_END_POINT_V1)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .returnResult(Item.class)
                        .getResponseBody();

        StepVerifier.create(responseBody.log("-- Value from the network : "))
                .expectNextCount(N_ITEMS)
                .verifyComplete();
    }

    @Test
    @DisplayName("When GET by ID of existing Item Then should return OK with item representation in the body")
    void getOneItem() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), DEFAULT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    System.out.println(response.getResponseBody());
                });
               // .jsonPath("$.description", "Item 1");
    }

    @Test
    @DisplayName("When GET by ID of non existing Item Then should return NOT_FOUND without item representation in the body")
    void getOnItemNotFound() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "non-existing")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("When POST item Then should return CREATED response with item representation in the body")
    void createItem() {
        Item item = new Item(null, "Iphone X", 150.0D);

        webTestClient.post()
                .uri(ItemConstants.ITEM_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo(item.getDescription())
                .jsonPath("$.price").isEqualTo(item.getPrice());
    }

    @Test
    @DisplayName("When POST item with invalid body Then should return Bad request response with item representation in the body")
    void createItemWithInvalidRequest() {
        Item item = new Item(null, " ", -150.0D);

        webTestClient.post()
                .uri(ItemConstants.ITEM_END_POINT_V1)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.type").isEqualTo("https://yourdomain.com/errors/400")
                .jsonPath("$.title").isEqualTo("Bad Request")
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void deleteItem() {
        webTestClient.delete()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/").concat(DEFAULT_ID))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
    }

    @Test
    @DisplayName("When PUT Item with existing ID Then Should update the item updated")
    void updateItem() {
        Item requestPayload = new Item(DEFAULT_ID, "Beats HeadPhones", 145.99D);
        webTestClient.put()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/").concat(DEFAULT_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestPayload), Item.class)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(requestPayload.getPrice())
                .jsonPath("$.description").isEqualTo(requestPayload.getDescription());
    }

    @Test
    @DisplayName("When PUT Item with non-existing ID Then should return not found")
    void updateItemNotFound() {
        Item requestPayload = new Item("non-existing", "Beats HeadPhones", 145.99D);
        webTestClient.put()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/").concat("non-existing"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestPayload), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void handleException() {
        webTestClient.get()
                .uri(ItemConstants.ITEM_END_POINT_V1 + "/runtime-exception")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .consumeWith(response -> {
                    System.out.println(response.getResponseBody());
                })
                .jsonPath("$.type").isEqualTo("https://yourdomain.com/errors/500")
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.title").isEqualTo("Internal Server Error")
                .jsonPath("$.detail").isEqualTo("An unexpected error occurred")
                .jsonPath("$.instance").isEqualTo("/v1/items/runtime-exception");

        webTestClient.get()
                .uri(ItemConstants.ITEM_END_POINT_V1 + "/runtime-exception")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .consumeWith(entityExchangeResult -> {

                    final byte[] responseBody = entityExchangeResult.getResponseBody();
                    Assertions.assertNotNull(responseBody);
                    System.out.println(new String(responseBody));

                });
    }
}
