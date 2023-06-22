package io.github.jlmc.uploadcsv.application.port;

import io.github.jlmc.uploadcsv.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collection;
import java.util.List;


@Repository
public class LocationRepositoryImpl implements LocationsQueries {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Lazy
    @Autowired
    private LocationRepository repository;


    /**
     * Get Locations by Account Page.
     *
     * @param accountId the account id to apply the filter.
     * @param pageable  pagination information, the page number starts in the index 0.
     */
    @Override
    public Mono<Page<Location>> getLocationsByAccountId(String accountId, Pageable pageable) {
        Mono<Long> totalMono = repository.countByAccountId(accountId);
        Mono<List<Location>> contentMono = repository.findAllByAccountId(accountId, pageable).collectList();

        return Mono.zip(totalMono, contentMono)
                   .map((Tuple2<Long, List<Location>> it) -> {
                       Long total = it.getT1();
                       List<Location> content = it.getT2();
                       return new PageImpl<>(content, pageable, total);
                   });
    }

    @Override
    public Flux<String> findAllIdsByAccountIdAndIdIn(String accountId, Collection<String> ids) {
        Query query = new Query(Criteria.where("accountId").is(accountId).and("id").in(ids));
        query.fields().include("id");
        return reactiveMongoTemplate.find(query, Location.class).map(Location::getId);
    }

    @Override
    public Mono<Location> saveInactiveLocation(Location location) {
        return reactiveMongoTemplate.insert(location, Location.INACTIVE_LOCATIONS);
    }
}
