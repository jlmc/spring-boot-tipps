package io.github.jlmc.uploadcsv.adapters.in.rest;

import io.github.jlmc.uploadcsv.adapters.in.rest.mappers.LocationResourceMapper;
import io.github.jlmc.uploadcsv.adapters.in.rest.resources.LocationResource;
import io.github.jlmc.uploadcsv.application.port.LocationRepository;
import io.github.jlmc.uploadcsv.application.services.DeleteAccountLocationByIdInteractor;
import io.github.jlmc.uploadcsv.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Validated
@RestController

@RequestMapping(
        path = {"/locations"},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}
)
public class LocationsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationsController.class);

    private final LocationRepository repository;
    private final LocationResourceMapper locationResourceMapper;
    private final DeleteAccountLocationByIdInteractor deleteAccountLocationByIdInteractor;

    public LocationsController(LocationRepository repository,
                               LocationResourceMapper locationResourceMapper, DeleteAccountLocationByIdInteractor deleteAccountLocationByIdInteractor) {
        this.repository = repository;
        this.locationResourceMapper = locationResourceMapper;
        this.deleteAccountLocationByIdInteractor = deleteAccountLocationByIdInteractor;
    }

    @GetMapping(path = "/{accountId}")
    public Flux<LocationResource> getLocations(@PathVariable(name = "accountId") String accountId) {
        return repository.findByAccountId(accountId).map(locationResourceMapper::toResource);
    }

    @GetMapping(path = "/{accountId}/page")
    public Mono<Page<LocationResource>> getLocationPage(
            @PathVariable(name = "accountId") String accountId,
            @PageableDefault(page = 0, size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "address.countryName", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "address.regionName", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "address.city", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "id", direction = Sort.Direction.ASC),
            }) Pageable pageable) {
        return repository.getLocationsByAccountId(accountId, pageable)
                         .doFirst(() -> LOGGER.info("Get location page <{}> with size <{}>", pageable.getPageNumber(), pageable.getPageSize()))
                         .map((Page<Location> it) -> {
                             List<LocationResource> content = it.get().map(locationResourceMapper::toResource).toList();
                             return new PageImpl<>(content, pageable, it.getTotalElements());
                         });
    }

    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteByAccountId(@PathVariable(name = "accountId") String accountId) {
        return repository.deleteAllByAccountId(accountId);
    }

    @DeleteMapping("/{accountId}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteByAccountId(@PathVariable(name = "accountId") String accountId,
                                        @PathVariable(name = "id") String id) {
        return deleteAccountLocationByIdInteractor.deleteAccountLocationsById(accountId, Set.of(id));
    }
}
