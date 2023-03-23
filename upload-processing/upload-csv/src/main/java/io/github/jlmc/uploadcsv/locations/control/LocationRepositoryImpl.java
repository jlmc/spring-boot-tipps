package io.github.jlmc.uploadcsv.locations.control;

import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Collection;


@Repository
public class LocationRepositoryImpl implements LocationsQueries {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public LocationRepositoryImpl(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
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
