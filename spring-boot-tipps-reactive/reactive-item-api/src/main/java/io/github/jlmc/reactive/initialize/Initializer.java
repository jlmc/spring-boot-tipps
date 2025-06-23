package io.github.jlmc.reactive.initialize;

import io.github.jlmc.reactive.domain.model.Item;
import io.github.jlmc.reactive.domain.model.ItemCapped;
import io.github.jlmc.reactive.domain.repository.ItemCappedReactiveRepository;
import io.github.jlmc.reactive.domain.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Slf4j
@Profile("dev")
@Component
public class Initializer implements CommandLineRunner {

    private final ItemReactiveRepository repository;
    private final ReactiveMongoOperations mongoOperations;
    private final ItemCappedReactiveRepository itemCappedReactiveRepository;

    public Initializer(ItemReactiveRepository repository, ReactiveMongoOperations mongoOperations, ItemCappedReactiveRepository itemCappedReactiveRepository) {
        this.repository = repository;
        this.mongoOperations = mongoOperations;
        this.itemCappedReactiveRepository = itemCappedReactiveRepository;
    }

    @Override
    public void run(String... args) {
        initDataSetup();

        createCoppedCollection();
        dataSetupCoppedCollection();
    }

    private void initDataSetup() {
        repository.deleteAll()
                  .thenMany(Flux.fromIterable(data()))
                  .flatMap(repository::save)
                  .thenMany(repository.findAll())
                  .subscribe(item -> log.info("Item inserted from Const:  {}", item.toString()));
    }

    private List<Item> data() {
        return List.of(
           new Item(null, "Sony Smart TV", 1500.0D),
           new Item(null, "Mac Pro 15", 2000.0D),
           new Item(null, "Mac Pro 19", 3500.0D)
        );
    }

    private void createCoppedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);

        mongoOperations.createCollection(ItemCapped.class,
                    CollectionOptions.empty().maxDocuments(20).size(50_000).capped()
                );

    }

    public void dataSetupCoppedCollection() {
        final Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(60))
                                         .map(i -> new ItemCapped(null, "My Random item " + i, 123.89D));

        itemCappedReactiveRepository
                .insert(itemCappedFlux)
                .subscribe(itemCapped -> log.info("ItemCapped inserted : {}", itemCapped))
        ;
    }
}
