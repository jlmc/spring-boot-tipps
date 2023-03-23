package io.github.jlmc.uploadcsv.slots.control;

import io.github.jlmc.uploadcsv.slots.entity.Spot;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends ReactiveMongoRepository<Spot, String> {
}
