package io.github.jlmc.reactive.api.v1;

import io.github.jlmc.reactive.ItemConstants;
import io.github.jlmc.reactive.domain.model.ItemCapped;
import io.github.jlmc.reactive.domain.repository.ItemCappedReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
@RequestMapping(path = ItemConstants.ITEM_END_POINT_CAPPED_V1)
public class ItemCappedController {

    private final ItemCappedReactiveRepository repository;

    public ItemCappedController(ItemCappedReactiveRepository repository) {
        this.repository = repository;
    }


    /**
     * curl -i localhost:8080:/v1/items-capped
     * @return
     */
    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getAll() {
        return repository.findAllBy();
    }
}
