package io.github.jlmc.uploadcsv.application.services;

import io.github.jlmc.uploadcsv.application.port.LocationRepository;
import io.github.jlmc.uploadcsv.commons.UseCase;
import io.github.jlmc.uploadcsv.domain.Location;
import reactor.core.publisher.Flux;

@UseCase
public class GetAllAccountLocationsInteractor {

    private final LocationRepository locationRepository;

    public GetAllAccountLocationsInteractor(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Flux<Location> getAllAccountLocation(String accountId) {
        return locationRepository.findByAccountId(accountId);
    }
}
