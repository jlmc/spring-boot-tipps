package io.github.jlmc.uploadcsv.application.services;

import io.github.jlmc.uploadcsv.application.port.LocationRepository;
import io.github.jlmc.uploadcsv.commons.UseCase;
import io.github.jlmc.uploadcsv.domain.Location;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@UseCase
public class BulkUpsertAccountLocationsInteractor {

    private final LocationRepository locationRepository;

    public BulkUpsertAccountLocationsInteractor(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Flux<Location> process(final String accountId, final Collection<Location> locations) {
        return filterNotExistingIds(accountId, locations)
                .flatMapIterable(invalidIds -> {
                    checkAllIdsExists(invalidIds);
                    return locations;
                })
                .flatMap(dataLocation -> this.updateLocation(accountId, dataLocation))
                .flatMap(locationRepository::save);
    }

    private Mono<Location> updateLocation(String accountId, Location dataLocation) {
        if (dataLocation.getId() == null) {
            return Mono.just(dataLocation);
        } else {
            return locationRepository.findByAccountIdAndId(accountId, dataLocation.getId())
                                     .map(locationEntity -> locationEntity.update(dataLocation));
        }
    }


    private Mono<Set<String>> filterNotExistingIds(String accountId, Collection<Location> locations) {
        return Mono.just(locations.stream().map(Location::getId).filter(Objects::nonNull).collect(Collectors.toSet()))
                   .filter(it -> !it.isEmpty())
                   .flatMap(idThatMustBeFound ->
                           locationRepository.findAllIdsByAccountIdAndIdIn(accountId, idThatMustBeFound).collect(Collectors.toSet())
                                             .map(foundIds -> {
                                                 HashSet<String> strings = new HashSet<>(idThatMustBeFound);
                                                 strings.removeAll(foundIds);
                                                 return Set.copyOf(strings);
                                             }))
                   .switchIfEmpty(Mono.just(Collections.emptySet()));
    }

    private void checkAllIdsExists(Set<String> invalidIds) {
        if (!invalidIds.isEmpty()) {
            throw new LocationIdsNotFoundException(invalidIds);
        }
    }
}
