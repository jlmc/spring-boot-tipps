package io.github.jlmc.uof.infrastructure.database;

import io.github.jlmc.uof.domain.fruits.entities.Fruit;
import io.github.jlmc.uof.domain.fruits.ports.outgoing.FruitsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class InMemoryFruitsRepository implements FruitsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryFruitsRepository.class);

    private final Map<String, Fruit> fruits;

    public InMemoryFruitsRepository(Map<String, Fruit> fruits) {
        Objects.requireNonNull(fruits);
        this.fruits = Map.copyOf(fruits);
    }

    @Override
    public List<Fruit> getAll() {
        return fruits.values()
                .stream()
                .sorted(Fruit.BY_ID)
                .toList();
    }

    @Override
    public Optional<Fruit> findById(String id) {
        if (!fruits.containsKey(id)) {
            LOGGER.debug("Fruit with the id {} not exists on the repository", id);
            return Optional.empty();
        }

        return Optional.ofNullable(fruits.get(id));
    }

    @Override
    public Page<Fruit> getPage(Pageable pageable) {
        List<Fruit> allElements = getAll();

        long offset = pageable.getOffset();
        int pageSize = pageable.getPageSize();

        List<Fruit> pageElements = allElements.stream().skip(offset).limit(pageSize).toList();

        return new PageImpl<>(pageElements, pageable, allElements.size());
    }
}
