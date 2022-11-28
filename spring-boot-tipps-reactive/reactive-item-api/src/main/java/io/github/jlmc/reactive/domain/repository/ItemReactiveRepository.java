package io.github.jlmc.reactive.domain.repository;

import io.github.jlmc.reactive.domain.model.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {

    Flux<Item> findByDescription(String description);

}
