package io.github.jlmc.uploadcsv.locations.boundary.csv.locations;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model.Columns;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model.LocationCsvResource;

import java.util.Comparator;

final class LocationsColumnNameMappingStrategy {

    private LocationsColumnNameMappingStrategy() {
        throw new UnsupportedOperationException();
    }

    static HeaderColumnNameMappingStrategy<LocationCsvResource> columnNameMappingStrategy() {
        var strategy =
                new HeaderColumnNameMappingStrategyBuilder<LocationCsvResource>()
                        .withForceCorrectRecordLength(true)
                        .build();

        strategy.setType(LocationCsvResource.class);
        strategy.setColumnOrderOnWrite(Comparator.comparing(Columns::indexOf));

        return strategy;
    }
}
