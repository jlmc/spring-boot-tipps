package io.github.jlmc.reactive.handler;

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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
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
@ActiveProfiles("test")
class ItemsHandlerTest {

    public static final String DEFAULT_ID = "1234";
    private static final int N_ITEMS = 4;
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository repository;

/*
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            //if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("Request: \n");
                //append clientRequest method and url
                clientRequest
                        .headers()
                        .forEach((name, values) -> values.forEach(value -> */
/* append header key/value *//*
));

                System.out.println(sb.toString());
                //log.debug(sb.toString());
            //}
            return Mono.just(clientRequest);
        });
    }
*/




    @BeforeEach
    void setUp() {
        /*var a =
                WebClient.builder()
                         .filters(exchangeFilterFunctions -> {
                             exchangeFilterFunctions.add(logRequest());
    
                         }).build();*/
        
        repository.deleteAll()
                  .thenMany(Flux.fromIterable(data()))
                  .flatMap(repository::save)
                  .doOnNext(item -> System.out.println("Item inserted from Test:  " + item.toString()))
                  .blockLast(); // block to make sure the data is available for the tests cases
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
        List<Item> responseBody =
                webTestClient.get()
                             .uri(ItemConstants.ITEM_FUNCTIONAL_V1)
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
                     .uri(ItemConstants.ITEM_FUNCTIONAL_V1)
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
                             .uri(ItemConstants.ITEM_FUNCTIONAL_V1)
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
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_V1.concat("/{id}"), DEFAULT_ID)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.description", "Item 1");
    }

    @Test
    @DisplayName("When GET by ID of non existing Item Then should return NOT_FOUND without item representation in the body")
    void getOnItemNotFound() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_V1.concat("/{id}"), "non-existing")
                     .exchange()
                     .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("When POST item Then should return CREATED response with item representation in the body")
    void createItem() {
        Item item = new Item(null, "Iphone X", 150.0D);

        webTestClient.post()
                     .uri(ItemConstants.ITEM_FUNCTIONAL_V1)
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
    void deleteItem() {
        webTestClient.delete()
                     .uri(ItemConstants.ITEM_FUNCTIONAL_V1.concat("/").concat(DEFAULT_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().is2xxSuccessful()
                     .expectStatus().isNoContent()
                     .expectBody(Void.class);

        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_V1.concat("/{id}"), DEFAULT_ID)
                     .exchange()
                     .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("When PUT Item with existing ID Then Should update the item updated")
    void updateItem() {
        Item requestPayload = new Item(DEFAULT_ID, "Beats HeadPhones", 145.99D);
        webTestClient.put()
                     .uri(ItemConstants.ITEM_FUNCTIONAL_V1.concat("/").concat(DEFAULT_ID))
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
                     .uri(ItemConstants.ITEM_FUNCTIONAL_V1.concat("/").concat("non-existing"))
                     .contentType(MediaType.APPLICATION_JSON)
                     .accept(MediaType.APPLICATION_JSON)
                     .body(Mono.just(requestPayload), Item.class)
                     .exchange()
                     .expectStatus().isNotFound();
    }

}
