package io.github.jlmc.uploadcsv.locations.control;

import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LocationRepository extends ReactiveMongoRepository<Location, String>, LocationsQueries {

    Mono<Location> findByAccountIdAndId(String accountId, String id);

    Flux<Location> findByAccountId(String accountId);
}
