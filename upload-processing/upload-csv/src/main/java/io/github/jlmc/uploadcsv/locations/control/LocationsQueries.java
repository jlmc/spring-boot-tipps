package io.github.jlmc.uploadcsv.locations.control;

import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface LocationsQueries {

    Sort SORT_BY_ADDRESS_COUNTRY_REGION_CITY_AND_ID =
            Sort.by(Sort.Direction.ASC, "address.countryName", "address.regionName", "address.city", "id");

    Mono<Page<Location>> getLocationsByAccountId(String accountId, Pageable pageable);

    Flux<String> findAllIdsByAccountIdAndIdIn(String accountId, Collection<String> ids);

    /**
     * Get Locations by Account Page order by address country code, region code, city and id.
     *
     * @param accountId the account id to apply the filter
     * @param page      page number, starting in the index 0
     * @param perPage   max number of elements per page
     */
    default Mono<Page<Location>> getLocationsByAccountIdOrderedByCountryCodeAndId(
            String accountId,
            Integer page,
            Integer perPage
    ) {
        return getLocationsByAccountId(
                accountId,
                PageRequest.of(page, perPage, SORT_BY_ADDRESS_COUNTRY_REGION_CITY_AND_ID)
        );
    }
}
