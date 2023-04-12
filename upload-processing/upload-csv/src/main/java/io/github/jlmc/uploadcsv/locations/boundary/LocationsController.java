package io.github.jlmc.uploadcsv.locations.boundary;

import io.github.jlmc.uploadcsv.locations.control.LocationRepository;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Validated
@RestController

@RequestMapping(
        path = {"/locations"},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}
)
public class LocationsController {

    public final LocationRepository repository;

    public LocationsController(LocationRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "/{accountId}")
    public Flux<Location> getLocations(@PathVariable("accountId") String accountId) {
        return repository.findByAccountId(accountId);
    }
}
