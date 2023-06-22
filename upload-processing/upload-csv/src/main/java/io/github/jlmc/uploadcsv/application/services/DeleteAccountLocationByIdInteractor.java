package io.github.jlmc.uploadcsv.application.services;

import io.github.jlmc.uploadcsv.application.port.LocationRepository;
import io.github.jlmc.uploadcsv.commons.UseCase;
import io.github.jlmc.uploadcsv.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Set;

@UseCase
public class DeleteAccountLocationByIdInteractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAccountLocationByIdInteractor.class);

    private final LocationRepository locationRepository;

    DeleteAccountLocationByIdInteractor(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Mono<Void> deleteAccountLocationsById(String accountId, Set<String> locationIds) {
        return Flux.fromIterable(locationIds)
                .flatMap(locationId -> deleteAccountLocationById(accountId, locationId))
                .collectList()
                .then();
    }

    private Mono<Void> deleteAccountLocationById(String accountId, String locationId) {
        return fetchLocation(accountId, locationId)
                .flatMap(location -> deleteLocation(accountId, location))
                .doFirst(() -> LOGGER.info("Starting deleting flow for location <{}> in the accountId <{}>", locationId, accountId))
                .doOnSuccess(removedLocation -> {
                    if (removedLocation != null) {
                        saveLocationIntoInactives(removedLocation);
                    }
                }).then();
    }

    private Mono<Location> deleteLocation(String accountId, Location location) {
        return locationRepository.deleteByAccountIdAndId(accountId, location.getId())
                .then(Mono.just(location))
                .doOnError(th -> {
                    LOGGER.error("Error deleting location <{}> in the account <{}>", location.getId(), accountId, th);
                }).onErrorResume(th -> Mono.empty());
    }

    private void saveLocationIntoInactives(Location location) {
        locationRepository.saveInactiveLocation(location)
                .doFirst(() -> LOGGER.info("Add the location <{}> into the inactive ones", location.getId()))
                .doOnError(th -> LOGGER.error("Some unexpected error happens add the location <{}> into the inactive ones", location.getId(), th))
                .subscribeOn(Schedulers.boundedElastic()).subscribe();
    }

    private Mono<Location> fetchLocation(String accountId, String locationId) {
        return this.locationRepository.findByAccountIdAndId(accountId, locationId)
                .doOnError(th -> LOGGER.error("Error fetching location id <{}> in the account <{}>", locationId, accountId, th))
                .onErrorResume((th) -> Mono.empty());
    }


}
