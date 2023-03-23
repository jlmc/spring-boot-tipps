package io.github.jlmc.uploadcsv.fluz;

import reactor.core.publisher.Flux;

import java.util.List;

public class LocationRepository {

    private final List<String> ids;

    public LocationRepository(List<String> ids) {
        this.ids = ids;
    }

    public Flux<String> findAllByAccountIdAndIdIn() {
        return Flux.fromIterable(ids);
    }
}
