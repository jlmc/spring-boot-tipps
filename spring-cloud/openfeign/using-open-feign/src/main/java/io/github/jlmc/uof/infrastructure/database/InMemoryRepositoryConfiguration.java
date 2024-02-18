package io.github.jlmc.uof.infrastructure.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.uof.domain.fruits.entities.Fruit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Configuration
public class InMemoryRepositoryConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryRepositoryConfiguration.class);

    private static final TypeReference<List<Fruit>> LIST_FRUITS = new TypeReference<List<Fruit>>() {};


    @Value("classpath:fruits-doubles.json")
    Resource resourceFile;

    @Bean
    InMemoryFruitsRepository inMemoryFruitsRepository(ObjectMapper objectMapper) {
        Map<String, Fruit> elements = readFruits(objectMapper);

        return new InMemoryFruitsRepository(elements);
    }

    private Map<String, Fruit> readFruits(ObjectMapper objectMapper) {
        try {
            LOGGER.debug("Loading Fruits");
            var list = objectMapper.readValue(resourceFile.getInputStream(), LIST_FRUITS);

            LOGGER.debug("Indexing, the loaded fruits.");
            Map<String, Fruit> map =
                    list.stream()
                            .collect(toMap(Fruit::id, identity()));

            LOGGER.debug("Loading and indexing finished successful.");

            return map;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
