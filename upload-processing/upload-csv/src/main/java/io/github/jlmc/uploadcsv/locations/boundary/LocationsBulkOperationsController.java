package io.github.jlmc.uploadcsv.locations.boundary;

import io.github.jlmc.uploadcsv.heroes.boundary.ApiDocs;
import io.github.jlmc.uploadcsv.locations.boundary.csv.CsvConstraintViolationsException;
import io.github.jlmc.uploadcsv.locations.boundary.csv.CsvReader;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.LocationsCsvWriter;
import io.github.jlmc.uploadcsv.locations.control.BulkUpsertAccountLocationsInteractor;
import io.github.jlmc.uploadcsv.locations.control.GetAllAccountLocationsInteractor;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import io.netty.buffer.ByteBufAllocator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "locations service", description = "the locations API with description tag annotation")


@Validated
@RestController

@RequestMapping(
        path = {"/locations-bulk"},
        //consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}
)
public class LocationsBulkOperationsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationsBulkOperationsController.class);
    public static final String ACCOUNT_ID = "123";

    //@Autowired
    //private CsvWriterService csvWriterService;


    @Autowired
    private CsvReader<Location> locationCsvReader;

    @Autowired
    private LocationsCsvWriter locationsCsvWriter;

    @Autowired
    private BulkUpsertAccountLocationsInteractor bulkUpsertAccountLocationsInteractor;

    @Autowired
    private GetAllAccountLocationsInteractor getAllAccountLocationsInteractor;



    //@GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)


    @Operation(
            operationId = "exportLocations",
            summary = "exports bulk csv content",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "exported with success",
                            headers = {
                                    @Header(name = HttpHeaders.CONTENT_TYPE, required = true, schema = @Schema(implementation = String.class, example = "text/csv")),
                                    @Header(name = HttpHeaders.CONTENT_DISPOSITION, required = true, schema = @Schema(implementation = String.class, example = "attachment; filename=locations-123.csv"))
                            },
                            content = {
                                    @Content(mediaType = "text/csv",
                                            schema = @Schema(implementation = String.class),
                                            examples = {@ExampleObject(value = ApiDocs.EXAMPLE)})
                            }
                    )
            }
    )



    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<Void> importLocationsInBulk(@RequestPart("file") FilePart filePart) {
        String accountId = ACCOUNT_ID;
        var s =
                filePart.content()
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

                            List<Location> items = results.items();

                            return items;
                        }).map(locations -> this.bulkUpsertAccountLocationsInteractor.process(accountId, locations))
                        .flatMap(Flux::collectList)
                        .then();
        return s;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Mono<Resource>> downloadCsv() {
        String fileName = String.format("%s.csv", RandomStringUtils.randomAlphabetic(10));

        Mono<List<Location>> listMono = this.getAllAccountLocationsInteractor.getAllAccountLocation(ACCOUNT_ID).collectList();
        Mono<ByteArrayInputStream> byteArrayInputStreamMono = listMono.flatMap(items -> this.locationsCsvWriter.generateCsv(items));

        //Mono<ByteArrayInputStream> byteArrayInputStreamMono = csvWriterService.generateCsv();



        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                             // .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                             .header(HttpHeaders.CONTENT_TYPE, "text/csv")

                             .body(byteArrayInputStreamMono
                                                   .flatMap((ByteArrayInputStream x) -> {
                                                       Resource resource = new InputStreamResource(x);
                                                       return Mono.just(resource);
                                                   }));
    }

    @GetMapping("/ex1")
    public Mono<Void> downloadEx1(ServerHttpResponse response) throws IOException {



        ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;

        String fileName = "template.csv";
        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename="+fileName+"");
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);

        Flux<Location> allAccountLocation = getAllAccountLocationsInteractor.getAllAccountLocation(ACCOUNT_ID);

        Flux<DataBuffer> dataBufferFlux =
            allAccountLocation.collectList()
                          .flatMapMany(locations -> {
                              StringWriter stringWriter = new StringWriter();
                              this.locationsCsvWriter.write(locations, new StringWriter());
                              return Flux.just(stringBuffer(stringWriter.getBuffer().toString()));
                          })

                ;

        return zeroCopyResponse.writeWith(dataBufferFlux);
    }

    private static DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }
}
