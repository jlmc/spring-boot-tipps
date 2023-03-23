package io.github.jlmc.uploadcsv.locations.control;

import reactor.core.publisher.Flux;

import java.util.Collection;

public interface LocationsQueries {

    Flux<String> findAllIdsByAccountIdAndIdIn(String accountId, Collection<String> ids);

}
