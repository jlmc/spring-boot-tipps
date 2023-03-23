package io.github.jlmc.uploadcsv.heroes.boundary;

import io.github.jlmc.uploadcsv.heroes.control.HeroRepository;
import io.github.jlmc.uploadcsv.heroes.entity.Hero;
import io.github.jlmc.uploadcsv.heroes.entity.HeroRequestRepresentation;
import io.github.jlmc.uploadcsv.heroes.entity.HeroResponseRepresentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag(name = "Heroes service", description = "the heroes API with description tag annotation")


@Validated
@RestController

@RequestMapping(
        path = {"/heroes"},
        //consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class HeroesController {

   // private static final Path basePath = Paths.get("./src/main/resources/upload/");

    private static final Logger LOGGER = LoggerFactory.getLogger(HeroesController.class);

    @Autowired
    public HeroRepository heroRepository;

    @Operation(summary = "Get all the heroes")
    @GetMapping
    public Mono<List<HeroResponseRepresentation>> getHeroes() {
        return heroRepository.findAll()
                             .map(it -> new HeroResponseRepresentation(it.getId(), it.getName(), it.getNickName(), it.getCreatedDate()))
                             .collectList();
    }

    @PostMapping
    public Mono<HeroResponseRepresentation> createOneHero(@RequestBody HeroRequestRepresentation payload) {
        return Mono.just(payload).map(it -> Hero.createHero(it.name(), it.nickName()))
                   .flatMap(heroRepository::save)
                   .map(it -> new HeroResponseRepresentation(it.getId(), it.getName(), it.getNickName(), it.getCreatedDate()));
    }

    //@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})

    // https://github.com/vinsguru/vinsguru-blog-code-samples/tree/master/webflux/webflux-file-upload
    @PostMapping(
            path = "/_data",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )

    @Operation(
            operationId = "uploadHeroes",
            summary = "Upload bulk csv content",
            parameters = {
                    @Parameter(name = "user-name", required = false, schema = @Schema(implementation = String.class), example = "john.doe"),
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = {
                            @Content(
                                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                    schema =
                                        @Schema(name = "file",
                                                example = ApiDocs.EXAMPLE,
                                                implementation = ApiDocs.ApiContent.class
                                        )
                            )

                    }
            )

    )


    public Flux<String> createByCSV(@RequestPart("user-name") String name, @RequestPart("fileToUpload") Flux<FilePart> fileParts) {
        LOGGER.debug("We receive a file: " + name);

        //var basePath = Paths.get("./src/main/resources/upload/");
        var basePath = Paths.get("/Users/joao.morgado/Documents/junks/projects/spring/spring-boot-tipps/upload-processing/upload-csv/destinantion");

        //Flux<String> s  =
        //        fileParts.flatMap((FilePart r) -> r.content().flatMap(it -> it.asInputStream(true)));

        Flux<String> stringFlux = fileParts.flatMap(filePart ->
                filePart.content().map(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            return new String(bytes, StandardCharsets.UTF_8);
                        })
                        .map(this::processAndGetLinesAsList)
                        .flatMapIterable(Function.identity()));




        return stringFlux;
    }

    private List<String> processAndGetLinesAsList(String string) {

        Supplier<Stream<String>> streamSupplier = string::lines;
        streamSupplier.get().allMatch(line -> true);
        var isFileOk = true;

        return isFileOk ? streamSupplier.get().collect(Collectors.toList()) : new ArrayList<>();
    }
}
