package io.github.jlmc.uploadcsv.heroes.control;

import io.github.jlmc.uploadcsv.heroes.entity.Hero;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeroRepository extends ReactiveMongoRepository<Hero, String> {
}
