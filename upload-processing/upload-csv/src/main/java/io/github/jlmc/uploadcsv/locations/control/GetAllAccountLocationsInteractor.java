package io.github.jlmc.uploadcsv.locations.control;

import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GetAllAccountLocationsInteractor {

    private final LocationRepository locationRepository;

    public GetAllAccountLocationsInteractor(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Flux<Location> getAllAccountLocation(String accountId) {
        return locationRepository.findAll();
    }
}
