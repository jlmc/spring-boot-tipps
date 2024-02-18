package io.github.jlmc.uof.domain.fruits.core;

import io.github.jlmc.uof.domain.fruits.entities.Fruit;
import io.github.jlmc.uof.domain.fruits.ports.incoming.FruitQueryReader;
import io.github.jlmc.uof.domain.fruits.ports.outgoing.FruitsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FruitQueryReaderService implements FruitQueryReader {

    private final FruitsRepository fruitsRepository;

    public FruitQueryReaderService(FruitsRepository fruitsRepository) {
        this.fruitsRepository = fruitsRepository;
    }

    @Override
    public List<Fruit> getAll() {
        return fruitsRepository.getAll();
    }

    @Override
    public Page<Fruit> getPage(Pageable pageable) {
        return fruitsRepository.getPage(pageable);
    }
}
