package io.github.jlmc.uploadcsv.locations.boundary.csv.locations;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.github.jlmc.uploadcsv.csv.boundary.CsvWriter;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model.LocationCsvResource;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.springframework.stereotype.Component;

import java.io.Writer;
import java.util.Collection;

@Component
public class LocationsCsvWriter implements CsvWriter<Location> {

    private static StatefulBeanToCsv<LocationCsvResource> createBeanToCsv(Writer writer) {
        HeaderColumnNameMappingStrategy<LocationCsvResource> headerColumnNameMappingStrategy =
                LocationsColumnNameMappingStrategy.columnNameMappingStrategy();

        return new StatefulBeanToCsvBuilder<LocationCsvResource>(writer)
                .withQuotechar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withMappingStrategy(headerColumnNameMappingStrategy)
                .withOrderedResults(true)
                .withThrowExceptions(false)
                .build();
    }

    @Override
    public Writer write(Collection<Location> entities, Writer writer) {
        try {
            var resources = entities.stream().map(this::toResource).toList();

            StatefulBeanToCsv<LocationCsvResource> beanToCsv = createBeanToCsv(writer);

            beanToCsv.write(resources);
            return writer;
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IllegalStateException(e);
        }
    }

    private LocationCsvResource toResource(Location location) {
        return LocationCsvResource.from(location);
    }
}
