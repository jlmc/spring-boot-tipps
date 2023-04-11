package io.github.jlmc.uploadcsv.locations.boundary.csv;

import io.github.jlmc.uploadcsv.util.ByteArrayInOutStream;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

public interface CsvWriter<T> {

    default Mono<ByteArrayInputStream> generateCsv(Collection<T> entities) {
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

    Writer write(Collection<T> entities, Writer writer);
}
