package io.github.jlmc.uof.domain.fruits.ports.outgoing;

import io.github.jlmc.uof.domain.fruits.entities.Fruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FruitsRepository {

    List<Fruit> getAll();
    Optional<Fruit> findById(String id);

    Page<Fruit> getPage(Pageable pageable);
}
