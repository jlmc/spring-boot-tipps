package io.github.jlmc.uploadcsv.locations.boundary;

import io.github.jlmc.uploadcsv.locations.control.CsvWriter;
import io.github.jlmc.uploadcsv.util.ByteArrayInOutStream;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;


@Service
public class CsvWriterService {

    private final LocationPopulator locationPopulator;
    private final CsvWriter csvWriter;

    public CsvWriterService(LocationPopulator locationPopulator, CsvWriter csvWriter) {
        this.locationPopulator = locationPopulator;
        this.csvWriter = csvWriter;
    }


    public Mono<ByteArrayInputStream> generateCsv(){


        return Mono.fromCallable(() -> {
            try {
                ByteArrayInOutStream stream = new ByteArrayInOutStream();
                OutputStreamWriter streamWriter = new OutputStreamWriter(stream);

                Writer write = this.csvWriter.write(locationPopulator.locations(), streamWriter);
                streamWriter.flush();
                return stream.getInputStream();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }

        }).subscribeOn(Schedulers.boundedElastic());

    }
}
