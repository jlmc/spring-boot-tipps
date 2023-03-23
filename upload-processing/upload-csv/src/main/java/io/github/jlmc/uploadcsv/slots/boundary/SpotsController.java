package io.github.jlmc.uploadcsv.slots.boundary;

import io.github.jlmc.uploadcsv.slots.control.CsvDataWriterService;
import io.github.jlmc.uploadcsv.slots.control.CsvTemplateWriterService;
import io.github.jlmc.uploadcsv.slots.control.SpotRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;


/**
 * https://medium.com/@victortemitope95/how-to-write-and-download-a-csv-file-in-spring-webflux-5df8d817a597
 */
@CrossOrigin
@AllArgsConstructor
@Validated
@RestController
@RequestMapping(
        path = {"/spots"},
        //consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class SpotsController {

    private final CsvTemplateWriterService csvTemplateWriterService;

    private final CsvDataWriterService csvDataWriterService;

    private final SpotRepository spotRepository;

    @GetMapping(path = "/_empty")
    public ResponseEntity<Mono<Resource>> downloadEmptyFile() {
        String hash = RandomStringUtils.randomAlphabetic(10);
        String filename = String.format("template-%s.csv", hash);

        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                             //.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                             .contentType(MediaType.parseMediaType("application/csv"))
                             .body(csvTemplateWriterService.generateCsv()
                                                           .flatMap(x -> {
                                                               Resource resource = new InputStreamResource(x);
                                                               return Mono.just(resource);
                                                           }));
    }

    @GetMapping("/download-file")
    public Mono<Void> downloadFile(ServerHttpResponse response) throws IOException {

        ZeroCopyHttpOutputMessage zeroCopyResponse =
                (ZeroCopyHttpOutputMessage) response;

        String fileName = "template.csv";
        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename="+fileName+"");
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ClassPathResource resource = new ClassPathResource("csv/template.csv");
        File file = resource.getFile();
        return zeroCopyResponse.writeWith(file, 0,
                file.length());
    }


    @GetMapping("/download")
    public Mono<Void> download(ServerHttpResponse response) throws IOException {

        ZeroCopyHttpOutputMessage zeroCopyResponse =
                (ZeroCopyHttpOutputMessage) response;

        String fileName = "template.csv";
        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename="+fileName+"");
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ClassPathResource resource = new ClassPathResource("csv/template.csv");


       // Mono<List<Spot>> listMono = spotRepository.findAll().collectList();

        //listMono.flatMapMany(w ->Write)

        //Flux<DataBuffer> df = queryWorkDoneUseCase.findWorkDoneByIdSofkianoAndDateBetween(uid, startDate, endDate).collectList()
        //                                          .flatMapMany(workDoneList -> WriteCsvToResponse.writeWorkDone(workDoneList));

        Flux<DataBuffer> dataBufferFlux = csvDataWriterService.generateCsv();
        return zeroCopyResponse.writeWith(dataBufferFlux);
    }


        /*
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<String> createMonoByCSV(@RequestPart("user-name") String name, @RequestPart("fileToUpload") Mono<FilePart> fileParts) {
        var s1 = Flux.from(fileParts)
                     .flatMap(fp -> processor.process(fp))
                     .map(count -> String.format("%d", count));


        return s1;
    }

    @PostMapping(path = "/_data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<String> createFluxByCSV(@RequestPart("user-name") String name, @RequestPart("fileToUpload") Flux<FilePart> fileParts) {
        return Flux.just("done");
    }
     */
}


/*
https://stackoverflow.com/questions/67604967/read-csv-file-with-spring-webflux-and-opencsv
 */
