package io.github.jlmc.uploadcsv.slots.control;

import io.github.jlmc.uploadcsv.slots.entity.Spot;
import io.github.jlmc.uploadcsv.slots.entity.SpotLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

/**
 * https://www.youtube.com/watch?v=u3psCjCLM1w
 */

@Service
public class SpotsCsvProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotsCsvProcessor.class);
    private static final String CSV_SPLIT_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    @Autowired
    private SpotRepository spotRepository;

    public Mono<Long> process(FilePart fileParts) {

        fileParts.filename();
        var total =
                fileParts.content()
                         .map(DataBuffer::asInputStream)
                         .map(this::readStreamBytes)
                         .map(String::new)
                         .flatMapIterable(data -> {

                             List<String> allLines =
                                     Arrays.stream(data.split("\n")).toList();

                             //allLines.subList(1, allLines.size());
                             //String headers = allLines.get(0);
                             List<String> lines = allLines.stream().skip(1).toList();

                             return lines;
                         }).map(line -> line.trim().split(CSV_SPLIT_REGEX))
                         //.doOnNext(this::processLine)
                         .flatMap(this::processLine)
                         .flatMap((Spot entity) -> spotRepository.insert(entity))
                         //.subscribe(entity -> heroRepository.insert(entity))
                         .subscribeOn(Schedulers.boundedElastic())
                         .doFirst(() -> LOGGER.info("Starting processing the data"))
                         .doOnComplete(() -> LOGGER.info("Finished process the data"))
                         .doOnError(th -> LOGGER.error(th.getMessage()))
                         .subscribe();

        return Mono.just(98L);
    }

    private Mono<Spot> processLine(String[] lineColumnsData) {
        SpotLine.builder()
                .id(lineColumnsData[0])
                .name(lineColumnsData[1])
                .phoneNumber(lineColumnsData[2])
                .address(lineColumnsData[3])
                .build();


        Spot spot1 = Spot.builder()
                         .name(lineColumnsData[1])
                         .phoneNumber(lineColumnsData[2])
                         .address(lineColumnsData[3])
                         .build();

        return spotRepository.save(spot1);
    }

    private byte[] readStreamBytes(InputStream inputStream) {
        try {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
