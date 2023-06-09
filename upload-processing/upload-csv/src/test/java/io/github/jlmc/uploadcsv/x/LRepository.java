package io.github.jlmc.uploadcsv.x;

import io.github.jlmc.uploadcsv.locations.entity.Location;
import reactor.core.publisher.Mono;

public interface LRepository {
    Mono<Location> getLocationByAccountIdAndId(String account, String locationId);

    Mono<Void> deleteByAccountIdAndId(String accountId, String id);

    Mono<Location> saveInactiveLocation(Location location);
}
