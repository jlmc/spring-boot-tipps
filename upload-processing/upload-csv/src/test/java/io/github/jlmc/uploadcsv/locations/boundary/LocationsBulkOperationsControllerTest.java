package io.github.jlmc.uploadcsv.locations.boundary;

import io.github.jlmc.uploadcsv.ObjectFactory;
import io.github.jlmc.uploadcsv.adviser.boundary.ApiErrorsConfigurationProperties;
import io.github.jlmc.uploadcsv.adviser.boundary.ControllerAdvice;
import io.github.jlmc.uploadcsv.adviser.boundary.CsvIllegalDataExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.control.ConstraintViolationExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.control.CsvConstraintViolationsExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.control.MethodArgumentNotValidExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.control.WebExchangeBindExceptionHandler;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.LocationsCsvReader;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.LocationsCsvWriter;
import io.github.jlmc.uploadcsv.locations.control.BulkUpsertAccountLocationsInteractor;
import io.github.jlmc.uploadcsv.locations.control.GetAllAccountLocationsInteractor;
import io.github.jlmc.uploadcsv.locations.control.LocationRepository;
import io.github.jlmc.uploadcsv.locations.control.LocationRepositoryImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static io.github.jlmc.uploadcsv.BodyFactory.fileMultipart;
import static io.github.jlmc.uploadcsv.Resources.classPathResource;
import static io.github.jlmc.uploadcsv.Resources.resourceContent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(
        controllers = {
                LocationsBulkOperationsController.class
        },

        excludeAutoConfiguration = {
                MongoAutoConfiguration.class,
                MongoReactiveDataAutoConfiguration.class,
                MongoReactiveAutoConfiguration.class
        })
@Import({
        LocationsCsvReader.class,
        LocationsCsvWriter.class,
        ControllerAdvice.class,
        ApiErrorsConfigurationProperties.class,
        CsvIllegalDataExceptionHandler.class,
        CsvConstraintViolationsExceptionHandler.class,
        MethodArgumentNotValidExceptionHandler.class,
        ConstraintViolationExceptionHandler.class,
        WebExchangeBindExceptionHandler.class,
})
@EnableAutoConfiguration(exclude = {
        MongoReactiveAutoConfiguration.class,
        MongoAutoConfiguration.class,
        EmbeddedMongoAutoConfiguration.class,
        MongoReactiveRepositoriesAutoConfiguration.class
})
class LocationsBulkOperationsControllerTest {
    @MockBean
    private BulkUpsertAccountLocationsInteractor bulkUpsertAccountLocationsInteractor;
    @MockBean
    private GetAllAccountLocationsInteractor getAllAccountLocationsInteractor;

    @MockBean
    LocationRepository locationRepository;

    @MockBean
    LocationRepositoryImpl locationRepositoryImpl;

    @Autowired
    private WebTestClient webClient;

    @Nested
    class downloadCsvFile {

        @Test
        void whenExistsLocations() {
            when(getAllAccountLocationsInteractor.getAllAccountLocation(eq("1")))
                    .thenReturn(Flux.fromIterable(List.of(ObjectFactory.location("1"), ObjectFactory.location("2"))));

            String expected = resourceContent(classPathResource("payloads/locations/responses/download-csv.csv"));

            webClient.get()
                     .uri(uriBuilder -> uriBuilder.path("/locations-bulk/{accountId}")
                                                  .build("1"))
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader()
                     .contentTypeCompatibleWith("text/csv")
                     .expectBody()
                     .consumeWith(it -> {
                         String s = new String(Objects.requireNonNull(it.getResponseBody()), StandardCharsets.UTF_8);
                         assertEquals(expected, s);
                     });
        }
    }

    @Nested
    class ImportLocationsInBulk {
        @Test
        void validFile() {
            var resource = classPathResource("payloads/locations/post-locations-bulk-inserts.csv");

            final String accountId = "1";
            when(bulkUpsertAccountLocationsInteractor.process(anyString(), anyCollection()))
                    .thenReturn(Flux.fromIterable(List.of(ObjectFactory.location(accountId))));

            webClient.post()
                     .uri(uriBuilder -> uriBuilder.path("/locations-bulk/{accountId}")
                                                  .build(accountId))
                     .body(fileMultipart(resource))
                     .exchange()
                     .expectStatus().isNoContent();

            verify(bulkUpsertAccountLocationsInteractor, Mockito.times(1)).process(Mockito.eq(accountId), anyCollection());
        }

        @Test
        void fileWithViolations() {
            var resource = classPathResource("payloads/locations/post-locations-bulk-with-violations.csv");
            var responseResource = classPathResource("payloads/locations/responses/fileWithViolations.json");

            final String accountId = "1";
            when(bulkUpsertAccountLocationsInteractor.process(anyString(), anyCollection()))
                    .thenReturn(Flux.fromIterable(List.of(ObjectFactory.location(accountId))));

            webClient.post()
                     .uri(uriBuilder -> uriBuilder.path("/locations-bulk/{accountId}")
                                                  .build(accountId))
                     .body(fileMultipart(resource))
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectHeader()
                     .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                     .expectBody().json(resourceContent(responseResource), true);

            verify(bulkUpsertAccountLocationsInteractor, never()).process(any(), anyCollection());
        }
    }
}
