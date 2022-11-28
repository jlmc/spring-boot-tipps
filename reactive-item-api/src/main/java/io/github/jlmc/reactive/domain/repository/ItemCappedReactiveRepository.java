package io.github.jlmc.reactive.domain.repository;

import io.github.jlmc.reactive.domain.model.ItemCapped;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ItemCappedReactiveRepository extends ReactiveMongoRepository<ItemCapped, String> {

    @Tailable
    Flux<ItemCapped> findAllBy();

}
