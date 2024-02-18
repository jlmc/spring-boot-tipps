package io.github.jlmc.uof.domain.fruits.ports.incoming;

import io.github.jlmc.uof.domain.fruits.entities.Fruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FruitQueryReader {

    List<Fruit> getAll();

    Page<Fruit> getPage(Pageable pageable);
}
