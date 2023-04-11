package io.github.jlmc.uploadcsv.locations.boundary.csv.locations;

import com.opencsv.CSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.github.jlmc.uploadcsv.locations.boundary.csv.CsvWriter;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model.LocationCsvResource;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import io.github.jlmc.uploadcsv.util.ByteArrayInOutStream;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

@Component
public class LocationsCsvWriter implements CsvWriter<Location> {

    private static StatefulBeanToCsv<LocationCsvResource> createBeanToCsv(Writer writer) {
        HeaderColumnNameMappingStrategy<LocationCsvResource> headerColumnNameMappingStrategy =
                LocationsColumnNameMappingStrategy.columnNameMappingStrategy();

        return new StatefulBeanToCsvBuilder<LocationCsvResource>(writer)
                .withQuotechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                //.withLineEnd(RFC4180_LINE_END)
                .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withMappingStrategy(headerColumnNameMappingStrategy)
                .withOrderedResults(true)
                .withThrowExceptions(false)
                .build();
    }

    @Override
    public Mono<ByteArrayInputStream> generateCsv(Collection<Location> entities) {
        return Mono.fromCallable(() -> {
            try {
                ByteArrayInOutStream stream = new ByteArrayInOutStream();
                OutputStreamWriter streamWriter = new OutputStreamWriter(stream);

                write(entities, streamWriter);

                streamWriter.flush();
                return stream.getInputStream();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Writer write(Collection<Location> locations, Writer writer) {
        try {
            var resources = locations.stream().map(this::toResource).toList();

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
