package io.github.jlmc.uploadcsv.adapters.in.rest.csv;

import io.github.jlmc.uploadcsv.adapters.in.rest.csv.models.AddressCsvResource;
import io.github.jlmc.uploadcsv.adapters.in.rest.csv.models.LocationCsvResource;
import io.github.jlmc.uploadcsv.domain.Location;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LocationsCsvReader extends AbstractCsvReader<Location, LocationCsvResource> {

    public LocationsCsvReader() {
        super(LocationsColumnNameMappingStrategy::columnNameMappingStrategy);
    }

    @Override
    protected List<Location> toEntities(String accountId, List<LocationCsvResource> resources) {
        return resources.stream().map(it -> toEntity(accountId, it)).toList();
    }

    private Location toEntity(String accountId, LocationCsvResource locationCsvResource) {
        return Location.builder()
                       .id(locationCsvResource.getId())
                       .accountId(accountId)
                       .name(locationCsvResource.getName())
                       .phoneNumber(locationCsvResource.getPhoneNumber())
                       .timeZone(locationCsvResource.getTimeZone())
                       .address(Optional.ofNullable(locationCsvResource.getAddress()).map(AddressCsvResource::toEntity).orElse(null))
                       .imageUrl(locationCsvResource.getImageUrl())
                       .openHours(locationCsvResource.getOpenHours())
                       .build();
    }
}
