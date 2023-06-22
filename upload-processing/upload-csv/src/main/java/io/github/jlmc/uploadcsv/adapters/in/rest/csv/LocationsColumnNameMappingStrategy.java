package io.github.jlmc.uploadcsv.adapters.in.rest.csv;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import io.github.jlmc.uploadcsv.adapters.in.rest.csv.models.Columns;
import io.github.jlmc.uploadcsv.adapters.in.rest.csv.models.LocationCsvResource;

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
