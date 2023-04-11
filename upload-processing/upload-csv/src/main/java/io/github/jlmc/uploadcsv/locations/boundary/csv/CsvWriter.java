package io.github.jlmc.uploadcsv.locations.boundary.csv;

import io.github.jlmc.uploadcsv.locations.entity.Location;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.Writer;
import java.util.Collection;

public interface CsvWriter<T> {

    Mono<ByteArrayInputStream> generateCsv(Collection<T> entities);

    Writer write(Collection<Location> locations, Writer writer);
}
