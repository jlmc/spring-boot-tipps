package io.github.jlmc.uploadcsv.locations.control;

import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LocationRepository extends ReactiveMongoRepository<Location, String>, LocationsQueries {

    Mono<Location> findByAccountIdAndId(String accountId, String id);

    @Query(value = "{'accountId': ?0}",
            sort = "{'address.countryName': 1, 'address.city': 1, 'id': 1}"
    )
    Flux<Location> findByAccountId(String accountId);

    Mono<Long> countByAccountId(String accountId);

    Flux<Location> findAllByAccountId(String accountId, Pageable pageable);

    Mono<Void> deleteAllByAccountId(String accountId);

    Mono<Void> deleteByAccountIdAndId(String accountId, String id);
}
