package io.github.jlmc.uploadcsv.adapters.in.rest;

import io.github.jlmc.uploadcsv.adapters.in.rest.csv.CsvConstraintViolationsException;
import io.github.jlmc.uploadcsv.adapters.in.rest.csv.CsvReader;
import io.github.jlmc.uploadcsv.adapters.in.rest.csv.CsvWriter;
import io.github.jlmc.uploadcsv.application.services.BulkUpsertAccountLocationsInteractor;
import io.github.jlmc.uploadcsv.application.services.GetAllAccountLocationsInteractor;
import io.github.jlmc.uploadcsv.domain.Location;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Validated
@RestController

@RequestMapping(
        path = {"/locations-bulk"},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}
)
public class LocationsBulkOperationsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationsBulkOperationsController.class);

    private final CsvReader<Location> locationCsvReader;
    private final CsvWriter<Location> locationsCsvWriter;
    private final BulkUpsertAccountLocationsInteractor bulkUpsertAccountLocationsInteractor;
    private final GetAllAccountLocationsInteractor getAllAccountLocationsInteractor;

    public LocationsBulkOperationsController(CsvReader<Location> locationCsvReader,
                                             CsvWriter<Location> locationsCsvWriter,
                                             BulkUpsertAccountLocationsInteractor bulkUpsertAccountLocationsInteractor,
                                             GetAllAccountLocationsInteractor getAllAccountLocationsInteractor) {
        this.locationCsvReader = locationCsvReader;
        this.locationsCsvWriter = locationsCsvWriter;
        this.bulkUpsertAccountLocationsInteractor = bulkUpsertAccountLocationsInteractor;
        this.getAllAccountLocationsInteractor = getAllAccountLocationsInteractor;
    }

    @PostMapping(
            path = "/{accountId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<Void> importLocationsInBulk(@PathVariable("accountId") String accountId, @RequestPart("file") FilePart filePart) {

        return filePart.content()
                       .map(dataBuffer -> {
                           byte[] bytes = new byte[dataBuffer.readableByteCount()];
                           dataBuffer.read(bytes);
                           DataBufferUtils.release(dataBuffer);
                           return new String(bytes, StandardCharsets.UTF_8);
                       })
                       .reduce(new StringBuilder(), (acc, s1) -> {
                           acc.append(s1);
                           return acc;
                       })
                       .map(it -> {

                           var results = locationCsvReader.read(accountId, it.toString());

                           if (!results.isValid()) {
                               throw new CsvConstraintViolationsException(results.violations());
                           }

                           @SuppressWarnings("UnnecessaryLocalVariable")
                           List<Location> items = results.items();

                           return items;
                       }).map(locations -> this.bulkUpsertAccountLocationsInteractor.process(accountId, locations))
                       .flatMap(Flux::collectList)
                       .then();
    }

    @GetMapping(path = "/{accountId}")
    @ResponseBody
    public ResponseEntity<Mono<Resource>> exportLocations(@PathVariable("accountId") String accountId) {
        String fileName = String.format("%s.csv", RandomStringUtils.randomAlphabetic(10));

        Mono<List<Location>> listMono = this.getAllAccountLocationsInteractor.getAllAccountLocation(accountId).collectList();
        Mono<ByteArrayInputStream> byteArrayInputStreamMono = listMono.flatMap(this.locationsCsvWriter::generateCsv);

        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                             .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                             .body(byteArrayInputStreamMono
                                     .flatMap((ByteArrayInputStream x) -> {
                                         Resource resource = new InputStreamResource(x);
                                         return Mono.just(resource);
                                     }));
    }

    @GetMapping(path = "/_empty")
    public ResponseEntity<Mono<Resource>> downloadEmptyFile() {
        String hash = RandomStringUtils.randomAlphabetic(10);
        String filename = String.format("template-%s.csv", hash);

        var template = Mono.fromCallable(() -> {
            Resource resource = new ClassPathResource("csv/template.csv");
            return new FileInputStream(resource.getFile());
        });

        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                             .contentType(MediaType.parseMediaType("text/csv"))
                             .body(template.flatMap(x -> {
                                 Resource resource = new InputStreamResource(x);
                                 return Mono.just(resource);
                             }));
    }
}
