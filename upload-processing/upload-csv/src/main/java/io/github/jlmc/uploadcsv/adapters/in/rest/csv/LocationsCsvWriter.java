package io.github.jlmc.uploadcsv.adapters.in.rest.csv;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.github.jlmc.uploadcsv.adapters.in.rest.csv.models.LocationCsvResource;
import io.github.jlmc.uploadcsv.domain.Location;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Collection;

@Component
public class LocationsCsvWriter implements CsvWriter<Location> {

    private final HeaderColumnNameMappingStrategy<LocationCsvResource> headerColumnNameMappingStrategy =
            LocationsColumnNameMappingStrategy.columnNameMappingStrategy();

    private StatefulBeanToCsv<LocationCsvResource> createBeanToCsv(Writer writer) {

        return new StatefulBeanToCsvBuilder<LocationCsvResource>(writer)
                .withQuotechar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withMappingStrategy(headerColumnNameMappingStrategy)
                .withOrderedResults(true)
                .withThrowExceptions(false)
                .withApplyQuotesToAll(false)
                .build();
    }

    private CSVWriter createFallbackHeaderWriter(Writer writer) {
        return new CSVWriter(
                writer,
                ICSVWriter.DEFAULT_SEPARATOR,
                ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                ICSVWriter.DEFAULT_LINE_END
        );
    }

    public Writer write(Collection<Location> entities, Writer writer) {
        try {
            var resources = entities.stream().map(this::toResource).toList();

            if (!resources.isEmpty()) {
                createBeanToCsv(writer).write(resources);
            } else {
                createFallbackHeaderWriter(writer)
                        .writeNext(
                                headerColumnNameMappingStrategy.generateHeader(new LocationCsvResource()),
                                false
                        );
            }

            writer.flush();
            return writer;
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private LocationCsvResource toResource(Location location) {
        return LocationCsvResource.from(location);
    }
}
