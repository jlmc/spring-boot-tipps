package io.github.jlmc.uploadcsv.x;

import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;
import java.util.Set;

public class DeleteAccountLocationByIdInteractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAccountLocationByIdInteractor.class);

    private final LRepository locationRepository;

    DeleteAccountLocationByIdInteractor(LRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    Mono<Void> deleteAccountLocationsById(String accountId, Set<String> locationIds) {
        return Flux.fromIterable(locationIds)
                .flatMap(locationId -> this.deleteAccountLocationById(accountId, locationId)).collectList().then();
    }

    private Mono<Void> deleteAccountLocationById(String accountId, String locationId) {
        return locationRepository.getLocationByAccountIdAndId(accountId, locationId)
                .doOnError(th -> {
                    LOGGER.error("Error getLocationByAccountIdAndId the <{}>", locationId, th);
                })
                .onErrorResume((y) -> {
                    return Mono.empty();
                })
                .filter(Objects::nonNull)
                .flatMap(location -> {

                    return locationRepository.deleteByAccountIdAndId(accountId, location.getId())
                            .then(Mono.defer(() -> {
                                return Mono.just(location);
                            }))
                            .doOnError(th -> {
                                LOGGER.error("Error deleting the <{}>", locationId, th);
                            })
                            .onErrorResume((y) -> {
                                return Mono.empty();
                            });
                })
                .doFirst(() -> {
                    LOGGER.info("Deleting the location %s for account %s".formatted(locationId, accountId));
                }).doOnSuccess(removedLocation -> {
                    if (removedLocation != null) {
                        saveLocationIntoInactives(removedLocation);
                    }
                }).then();
    }

    private void saveLocationIntoInactives(Location location) {
        locationRepository.saveInactiveLocation(location)
                .doFirst(() -> LOGGER.info("Add the location <{}> into the inactive ones", location.getId()))
                .doOnError(th -> LOGGER.error("Some unexpected error happens add the location <{}> into the inactive ones", location.getId(), th))
                .subscribeOn(Schedulers.boundedElastic()).subscribe();
    }

}
