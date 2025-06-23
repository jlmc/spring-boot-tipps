package io.github.jlmc.reactive.domain.repository;

import io.github.jlmc.reactive.domain.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "de.flapdoodle.mongodb.embedded.version=6.0.5"
})
@ActiveProfiles("mongodb-embedded-test")
class ItemReactiveRepositoryEmbeddedTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> docs =
            List.of(
                    new Item("a", "A", 1.0D),
                    new Item("c", "B", 1.0D),
                    new Item("b", "B", 2.0D));

    @BeforeEach
    void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(docs))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(it -> System.out.println("Inserted Item is: " + it))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        Flux<Item> items = itemReactiveRepository.findAll();

        StepVerifier.create(items)
                .expectSubscription()
                .expectNextCount(3L)
                .verifyComplete();
    }

    @Test
    void getItemById() {
        Mono<Item> items = itemReactiveRepository.findById("a");

        StepVerifier.create(items)
                .expectSubscription()
                .expectNextMatches(item -> "A".equals(item.getDescription()))
                .verifyComplete();
    }

    @Test
    void findItemByDescription() {

        StepVerifier.create(itemReactiveRepository.findByDescription("A").log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void saveItem() {
        Item item = new Item(null, "Google Home Mini", 30.0);
        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log("saved Item: "))
                .expectSubscription()
                .expectNextMatches(it -> it.getId() != null && "Google Home Mini".equals(it.getDescription()))
                .verifyComplete();
    }

    @Test
    void updateItem() {
        double newPrice = 520.00D;

        Flux<Item> updatedItem =
                itemReactiveRepository.findByDescription("A")
                        .map(item -> {
                            item.setPrice(newPrice);
                            return item;
                        })
                        .flatMap(item -> itemReactiveRepository.save(item));

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == newPrice)
                .verifyComplete();
    }

    @Test
    void deleteItem() {
        Flux<Void> deletedItem =
                itemReactiveRepository.findByDescription("A").log()
                        .map(Item::getId)
                        .flatMap(
                                itemId -> {
                                    return itemReactiveRepository.deleteById(itemId);
                                });

        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(docs.size() - 1)
                .verifyComplete();
    }

}