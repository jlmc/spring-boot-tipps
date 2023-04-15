package io.github.jlmc.uploadcsv.locations.control;

import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.jlmc.uploadcsv.ObjectFactory.location;
import static java.util.stream.Collectors.toCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * T run this tests, we need to add the dependency:
 * <pre>
 *
 * <dependency>
 *   <groupId>de.flapdoodle.embed</groupId>
 *   <artifactId>de.flapdoodle.embed.mongo</artifactId>
 *   <!--<version>3.4.11</version>-->
 * </dependency>
 * </pre>
 */
@Disabled
@DataMongoTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(
        properties = {
                "spring.mongodb.embedded.version=3.6.5",
                "spring.data.mongodb.database=test"
        }
)
@Import(EmbeddedMongoAutoConfiguration.class)
class LocationRepositoryTest {

    public static final String ACCOUNT_ID = "1";
    @Autowired
    LocationRepository repository;

    @Nested
    class CountLocationsByAccountIdTests {
        @Test
        @DisplayName("""
                when we get the count locations by account id,
                it should return the total of documents of the given account
                """
        )
        void countLocationsByAccountId() {
            List<Location> locsAccountId1 = IntStream.range(1, 6).mapToObj(it -> location(null, "1")).toList();
            List<Location> locsAccountId2 = IntStream.range(1, 11).mapToObj(it -> location(null, "2")).toList();

            var setup =
                    repository.deleteAll()
                              .thenMany(repository.saveAll(locsAccountId1))
                              .thenMany(repository.saveAll(locsAccountId2));

            Mono<Long> countByAccountId = repository.countByAccountId("1");

            StepVerifier.create(Mono.from(setup).thenMany(countByAccountId))
                        .assertNext(result -> assertEquals(locsAccountId1.size(), result))
                        .verifyComplete();
        }
    }

    @Nested
    class GetLocationsByAccountIdTests {
        @Test
        @DisplayName("""
                when we get the first page,
                it should return a Page with the correct documents
                """
        )
        public void getLocationsByAccountIdFirstPage() {
            var page = 0;
            var perPage = 3;
            var totalElements = 22;

            Flux<Location> setup = populateTestsRecords(totalElements);

            Mono<Page<Location>> getLocationsByAccountId =
                    repository.getLocationsByAccountIdOrderedByCountryCodeAndId(ACCOUNT_ID, page, perPage);

            StepVerifier.create(Mono.from(setup).log()
                                    .then(getLocationsByAccountId).log())
                        .assertNext(it -> {

                            assertEquals(page, it.getNumber());
                            assertEquals(Long.valueOf(totalElements), it.getTotalElements());
                            assertEquals(perPage, it.getContent().size());
                            assertEquals(8, it.getTotalPages());
                            assertEquals("Australia", it.getContent().get(0).getAddress().getCountryName());
                            assertEquals("Brazil", it.getContent().get(1).getAddress().getCountryName());
                            assertEquals("Portugal", it.getContent().get(2).getAddress().getCountryName());
                        })
                        .verifyComplete();
        }

        @Test
        @DisplayName("""
                when we get the last page,
                it should return a Page with the correct documents
                """
        )
        void getLocationsByAccountIdLastPage() {
            var page = 7;
            var perPage = 3;
            var totalElements = 22L;

            Flux<Location> setup = populateTestsRecords(((int) totalElements));

            var getLocationsByAccountId =
                    repository.getLocationsByAccountIdOrderedByCountryCodeAndId(
                            ACCOUNT_ID,
                            page,
                            perPage
                    );

            StepVerifier.create(Mono.from(setup).log().then(getLocationsByAccountId).log())
                        .assertNext(givenPage -> {
                                    assertEquals(page, givenPage.getNumber());
                                    assertEquals(totalElements, givenPage.getTotalElements());
                                    assertEquals(1, givenPage.getContent().size());
                                    assertEquals(8, givenPage.getTotalPages());
                                    assertEquals("South Africa", givenPage.getContent().get(0).getAddress().getCountryName());
                                }
                        )
                        .verifyComplete();
        }

        @Test
        @DisplayName("""
                when we get an not existing page,
                it should return a Page with empty content
                """
        )
        void getLocationsByAccountIdEmptyPage() {
            var page = 8;
            var perPage = 3;
            var totalElements = 22L;

            Flux<Location> setup = populateTestsRecords(((int) totalElements));

            var getLocationsByAccountId =
                    repository.getLocationsByAccountIdOrderedByCountryCodeAndId(
                            ACCOUNT_ID,
                            page,
                            perPage
                    );

            StepVerifier.create(Mono.from(setup).log().then(getLocationsByAccountId).log())
                        .assertNext(givenPage -> {
                                    assertEquals(page, givenPage.getNumber());
                                    assertEquals(totalElements, givenPage.getTotalElements());
                                    assertEquals(0, givenPage.getContent().size());
                                    assertEquals(8, givenPage.getTotalPages());
                                }
                        )
                        .verifyComplete();
        }

        @NotNull
        private Flux<Location> populateTestsRecords(int totalElements) {
            var locationsAccountId =
                    IntStream.range(0, totalElements - 3)
                             .mapToObj(it -> location(null, ACCOUNT_ID, "Portugal"))
                             .collect(toCollection(ArrayList::new));
            locationsAccountId.add(location(null, ACCOUNT_ID, "South Africa"));
            locationsAccountId.add(location(null, ACCOUNT_ID, "Australia"));
            locationsAccountId.add(location(null, ACCOUNT_ID, "Brazil"));

            return repository.deleteAll()
                             .thenMany(repository.saveAll(locationsAccountId))
                             .thenMany(repository.saveAll(IntStream.range(0, 10)
                                                                   .mapToObj(it -> location(null, "2"))
                                                                   .toList()));
        }
    }

    @Nested
    @DisplayName("when we get an location ids by account id")
    class GetAccountLocationIdsIn {

        @Test
        void getLocationsIds() {
            var ids = List.of(
                    "64075d0c350af9558924b62a",
                    "6433db6a16af4546950a2021",
                    "6433db6b16af4546950a2022",
                    "6433db6b16af4546950a2023"
            );

            var locations =
                    ids.stream()
                       .map(it -> location(it, "1"))
                       .collect(toCollection(ArrayList::new));

            List<Location> others1 = IntStream.range(0, 3).mapToObj(it -> location(null, "1")).toList();
            List<Location> others2 = IntStream.range(0, 10).mapToObj(it -> location(null, "other")).toList();
            locations.addAll(others1);
            locations.addAll(others2);

            var populateTestsRecords =
                    repository.deleteAll().thenMany(repository.saveAll(locations)).collectList();

            var findAllByAccountIdAndIdIn = repository.findAllIdsByAccountIdAndIdIn("1", ids.subList(0, 3));

            StepVerifier.create(Mono.from(populateTestsRecords).log().thenMany(findAllByAccountIdAndIdIn).log())
                        .expectNext(ids.get(0))
                        .expectNext(ids.get(1))
                        .expectNext(ids.get(2))
                        .verifyComplete();

        }
    }

    @Nested
    class GetLocationByAccountIdAndLocationIdTests {
        @Test
        void getLocationByAccountIdAndLocationId() {
            var l1 = "6433db6a16af4546950a2021";
            var accountId = "1";
            var expectedLocation = location(l1, accountId);

            Flux<Location> setup = repository.deleteAll().thenMany(repository.saveAll(
                    List.of(
                            expectedLocation,
                            location(null, accountId),
                            location(null, "2"))));

            Mono<Location> getLocationByAccountIdAndId = repository.findByAccountIdAndId("1", l1);

            StepVerifier
                    .create(Mono.from(setup).log().then(getLocationByAccountIdAndId).log())
                    .assertNext(given -> {
                        assertNotNull(given);
                        assertNotNull(given.getId());
                        assertEquals(l1, given.getId());
                        assertEquals(accountId, given.getAccountId());
                    })
                    .verifyComplete();
        }

        @Test
        void getNonExistentLocationByAccountIdAndLocationId() {
            var l1 = "6433db6a16af4546950a2021";
            var l2 = "6433db6a16af4546950a2022";
            var l3 = "6433db6a16af4546950a2023";

            Flux<Location> setup = repository.deleteAll().thenMany(repository.saveAll(
                    List.of(
                            location(l1, "2"),
                            location(l2, "2"),
                            location(l3, "1"))));

            Mono<Location> getLocationByAccountIdAndId = repository.findByAccountIdAndId("1", l1);

            StepVerifier
                    .create(Mono.from(setup).log().then(getLocationByAccountIdAndId).log())
                    .verifyComplete();
        }
    }
}
