package io.github.jlmc.uploadcsv.locations.control;

import io.github.jlmc.uploadcsv.locations.entity.Location;
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
        Mono<Long> count = repository.countByAccountId(accountId);
        Mono<List<Location>> listMono = repository.findAllByAccountId(accountId, pageable).collectList();

        return count.zipWhen(it -> listMono)
                     .map((Tuple2<Long, List<Location>> tuple) -> {
                         Long t1 = tuple.getT1();
                         List<Location> t2 = tuple.getT2();
                         return new PageImpl<>(t2, pageable, t1);
                     });
    }

    @Override
    public Flux<String> findAllIdsByAccountIdAndIdIn(String accountId, Collection<String> ids) {
        /*
        val query = Query(Criteria.where("accountId").`is`(accountId).and("id").`in`(ids))
        query.fields().include("id", "accountId", "name", "timeZone", "address")
        return reactiveMongoTemplate.find(query, Location::class.java).mapNotNull(Location::id)
        */
        Query query = new Query(Criteria.where("accountId").is(accountId).and("id").in(ids));
        query.fields().include("id");
        //return reactiveMongoTemplate.findDistinct(query, "id", "locations", String.class);
        return reactiveMongoTemplate.find(query, Location.class).map(Location::getId);
    }
}
