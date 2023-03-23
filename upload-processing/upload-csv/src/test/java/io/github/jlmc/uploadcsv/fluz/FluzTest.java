package io.github.jlmc.uploadcsv.fluz;

import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FluzTest {


    private final List<String> IDS = List.of( "2");

    private final LocationRepository locationRepository = new LocationRepository(IDS);

    @Test
    public void test1() {
        Location location1 = Location.builder().id("1").name("location 1").build();
        Location location2 = Location.builder().id("2").name("location 2").build();

        Result<Location> locationResult = new Result<>(List.of(location1, location2), Collections.emptyList());

        var resul = process(locationResult.items()).collectList().block();

        System.out.println(resul);
    }

    public Mono<List<String>> missingIds(String accountId, Collection<Location> locations) {
        Set<String> ids = locations.stream().map(Location::getId).filter(Objects::nonNull).collect(Collectors.toSet());

        if (ids.isEmpty()) return Mono.empty();

        var u =
                locationRepository.findAllByAccountIdAndIdIn().collect(Collectors.toSet())
                                  .map(foundIds -> ids.stream().filter(it -> !foundIds.contains(it)).toList())
                                  .flatMap(Mono::just);

        return u;
    }

    public Flux<Location> process(Collection<Location> items) {

        var missingIds = missingIds("x", items);

        var s1 =
                missingIds.map(mm -> checkAllIdsExists(mm, items) )
                        .flatMapIterable( it -> it)
                        .flatMap(this::saveOrUpdate);

        return s1;

    }

    @NotNull
    private Collection<Location> checkAllIdsExists(Collection<String> missingIds, Collection<Location> locations) {
        if (!missingIds.isEmpty()) {
            throw new IllegalArgumentException("Missing the ids <" + missingIds + ">");
        }
        return locations;
    }

    private Mono<Location> saveOrUpdate(Location location) {
        return Mono.just(location)
                   .doFirst(() -> System.out.println("Saving Location " + location));
    }
}

/*



return Mono.fromCallable { csvReader.read(accountId = accountId, content = content) }
            .map {
                if (!it.valid) {
                    throw LocationsBulkConstraintViolationException(it.violations)
                }
                it
            }
            .flatMapMany {
                checkAllIdsExist(accountId, it.items)
                    .flatMapIterable { _ -> it.items }
            }
            .flatMap { unManagedLocation ->
                unManagedLocation.id
                    ?.let { id ->
                        fetchAndUpdateExistingLocation(
                            accountId = accountId,
                            locationId = id,
                            location = unManagedLocation
                        )
                    } ?: Mono.just(unManagedLocation)
            }.flatMap(locationRepository::save)
            .subscribeOn(Schedulers.boundedElastic())
            .doFirst {
                logger.info("Applying csv locations in account <$accountId>")
            }
            .doOnError {
                logger.error(
                    "Error Applying csv locations in account" +
                            "<$accountId> caused by <${it.message}>",
                    it
                )
            }
 */
