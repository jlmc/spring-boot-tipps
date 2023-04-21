package io.github.jlmc.uploadcsv.locations.boundary;

import io.github.jlmc.uploadcsv.locations.control.LocationRepository;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @GetMapping(path = "/{accountId}/page")
    public Mono<Page<Location>> getLocationPage(
            @PathVariable("accountId") String accountId,
            @PageableDefault(page = 0, size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "address.countryName", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "address.regionName", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "address.city", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "id", direction = Sort.Direction.ASC),
            }) Pageable pageable) {
        return repository.getLocationsByAccountId(accountId, pageable);
    }
}
