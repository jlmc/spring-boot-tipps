package io.github.jlmc.reactive.domain.repository;

import io.github.jlmc.reactive.domain.model.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("mongodb-container-test")
class ItemReactiveRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer =
                    new MongoDBContainer("mongo:4.4.4")
                    .withReuse(true) // optional: reuse container across test runs
                    .withCommand("--replSet", "rs0");  // enable replica set

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ItemReactiveRepository victim;


    @Test
    void saveItem() {
        Item doc = new Item(null, "Google Home Mini", 30.0D);
                victim.save(doc)
                .as(StepVerifier::create)
                .expectNextMatches(saved -> saved.getId() != null)
                .verifyComplete();

        victim.findAll()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }



}