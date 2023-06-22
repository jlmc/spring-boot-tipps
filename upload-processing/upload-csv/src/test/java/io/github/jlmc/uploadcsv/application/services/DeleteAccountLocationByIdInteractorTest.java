package io.github.jlmc.uploadcsv.application.services;

import io.github.jlmc.uploadcsv.application.port.LocationRepository;
import io.github.jlmc.uploadcsv.domain.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAccountLocationByIdInteractorTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private DeleteAccountLocationByIdInteractor sut;

    @Test
    @DisplayName("when we try to delete a single location and all methods execute successful")
    void whenAllMethodsRunsOk() {
        String accountId = "ACCOUNT_ID";
        String locationId = "mockLocationId";
        Location location = Location.builder().id(locationId).name("tests instance").build();

        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.just(location));
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.empty().then());
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.any(Location.class)))
                .thenReturn(Mono.just(location));

        StepVerifier.withVirtualTime(() -> sut.deleteAccountLocationsById(accountId, Set.of(locationId)).log())
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(1))
                .then(() -> {
                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, times(1)).saveInactiveLocation(any());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when we try to delete a single location and we get an error fetching it from the DB")
    void whenFindByIdReturnError() {
        String accountId = "ACCOUNT_ID";
        String locationId = "mockLocationId";
        Location location = Location.builder().id(locationId).name("tests instance").build();

        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.error(new RuntimeException("--->Some expected error <getLocationByAccountIdAndId> !!! <---")));
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.empty());
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.any(Location.class)))
                .thenReturn(Mono.just(location));

        StepVerifier.withVirtualTime(() -> sut.deleteAccountLocationsById(accountId, Set.of(locationId)).log())
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(1))
                .then(() -> {
                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, never()).deleteByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, never()).saveInactiveLocation(any());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when we try to delete a single location and we get an empty fetching it from the DB")
    void whenFindByIdReturnEmpty() {
        String accountId = "ACCOUNT_ID";
        String locationId = "mockLocationId";
        Location location = Location.builder().id(locationId).name("tests instance").build();

        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.empty());
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.empty().then());
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.any(Location.class)))
                .thenReturn(Mono.just(location));

        StepVerifier.withVirtualTime(() -> sut.deleteAccountLocationsById(accountId, Set.of(locationId)).log())
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(1))
                .then(() -> {
                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, never()).deleteByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, never()).saveInactiveLocation(any());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when we try to delete a single location and we get an error deleting it from the DB")
    void whenDeleteByIdReturnsError() {
        String accountId = "ACCOUNT_ID";
        String locationId = "mockLocationId";
        Location location = Location.builder().id(locationId).name("tests instance").build();

        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.just(location));
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.error(new RuntimeException("--->Some expected error!!! <---")));
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.any(Location.class)))
                .thenReturn(Mono.just(location));

        StepVerifier.withVirtualTime(() -> sut.deleteAccountLocationsById(accountId, Set.of(locationId)).log())
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(1))
                .then(() -> {
                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, never()).saveInactiveLocation(any());
                })
                .verifyComplete();


    }

    @Test
    @DisplayName("when we try to delete a single location and we get an error saving it in inactive ones from the DB")
    void whenSaveInactiveReturnsError() {
        String accountId = "ACCOUNT_ID";
        String locationId = "mockLocationId";
        Location location = Location.builder().id(locationId).name("tests instance").build();

        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.just(location));
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.empty().then());
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.any(Location.class)))
                .thenReturn(Mono.error(new RuntimeException("Error: <saveInactiveLocation>")));

        StepVerifier.withVirtualTime(() -> sut.deleteAccountLocationsById(accountId, Set.of(locationId)).log())
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(1))
                .then(() -> {
                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, times(1)).saveInactiveLocation(any());
                })
                .verifyComplete();
    }

    //// multiple

    @Test
    @DisplayName("when we try to delete a multiple locations and all methods execute successful")
    void whenMultipleAllMethodsRunsOk() {
        String accountId = "ACCOUNT_ID";
        String locationIdA = "location A";
        Location locationA = Location.builder().id(locationIdA).name("tests instance A").build();
        String locationIdB = "location B";
        Location locationB = Location.builder().id(locationIdB).name("tests instance B").build();
        String locationIdC = "location C";
        Location locationC = Location.builder().id(locationIdC).name("tests instance C").build();

        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationIdA)))
                .thenReturn(Mono.just(locationA));
        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationIdB)))
                .thenReturn(Mono.just(locationB));
        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationIdC)))
                .thenReturn(Mono.just(locationC));

        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationIdA)))
                .thenReturn(Mono.empty().then());
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationIdB)))
                .thenReturn(Mono.empty().then());
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationIdC)))
                .thenReturn(Mono.empty().then());

        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.eq(locationA)))
                .thenReturn(Mono.just(locationA));
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.eq(locationB)))
                .thenReturn(Mono.just(locationB));
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.eq(locationC)))
                .thenReturn(Mono.just(locationC));

        Set<String> locationIds = Set.of(locationIdA, locationIdB, locationIdC);
        StepVerifier.withVirtualTime(() -> sut.deleteAccountLocationsById(accountId, locationIds).log())
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(1))
                .then(() -> {
                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationIdA));
                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationIdA));
                    verify(locationRepository, times(1)).saveInactiveLocation(eq(locationA));

                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationIdB));
                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationIdB));
                    verify(locationRepository, times(1)).saveInactiveLocation(eq(locationB));

                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationIdC));
                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationIdC));
                    verify(locationRepository, times(1)).saveInactiveLocation(eq(locationC));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when we try to delete multiple locations and in the middle of it (i.e. after already deleted some of the locations) we get an error fetching it from the DB")
    void whenWeTryToDeleteMultipleAndOneOfTheIdsResultInAErrorFetchingFromDb() {
        String accountId = "ACCOUNT_ID";
        String locationIdA = "location A";
        Location locationA = Location.builder().id(locationIdA).name("tests instance A").build();
        String locationIdB = "location B";
        Location locationB = Location.builder().id(locationIdB).name("tests instance B").build();
        String locationIdC = "location C";
        Location locationC = Location.builder().id(locationIdC).name("tests instance C").build();

        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationIdA)))
                .thenReturn(Mono.just(locationA));
        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationIdB)))
                .thenReturn(Mono.error(new RuntimeException("error fetching from db locationB")));
        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationIdC)))
                .thenReturn(Mono.just(locationC));

        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationIdA)))
                .thenReturn(Mono.empty().then());
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationIdB)))
                .thenReturn(Mono.empty().then());
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationIdC)))
                .thenReturn(Mono.empty().then());

        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.eq(locationA)))
                .thenReturn(Mono.just(locationA));
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.eq(locationB)))
                .thenReturn(Mono.just(locationB));
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.eq(locationC)))
                .thenReturn(Mono.just(locationC));

        Set<String> locationIds = Set.of(locationIdA, locationIdB, locationIdC);
        StepVerifier.withVirtualTime(() -> sut.deleteAccountLocationsById(accountId, locationIds).log())
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(1))
                .then(() -> {
                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationIdA));
                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationIdA));
                    verify(locationRepository, times(1)).saveInactiveLocation(eq(locationA));

                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationIdB));
                    verify(locationRepository, times(0)).deleteByAccountIdAndId(eq(accountId), eq(locationIdB));
                    verify(locationRepository, times(0)).saveInactiveLocation(eq(locationB));

                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationIdC));
                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationIdC));
                    verify(locationRepository, times(1)).saveInactiveLocation(eq(locationC));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when we try to delete multiple locations and in the middle of it (i.e. after already deleted some of the locations) we get an error fetching it from the DB")
    void whenWeTryToDeleteMultipleAndGetErrorDeletingOneOfThen() {
        String accountId = "ACCOUNT_ID";
        String locationIdA = "location A";
        Location locationA = Location.builder().id(locationIdA).name("tests instance A").build();
        String locationIdB = "location B";
        Location locationB = Location.builder().id(locationIdB).name("tests instance B").build();
        String locationIdC = "location C";
        Location locationC = Location.builder().id(locationIdC).name("tests instance C").build();

        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationIdA)))
                .thenReturn(Mono.just(locationA));
        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationIdB)))
                .thenReturn(Mono.just(locationB));
        lenient()
                .when(locationRepository.findByAccountIdAndId(eq(accountId), eq(locationIdC)))
                .thenReturn(Mono.just(locationC));

        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationIdA)))
                .thenReturn(Mono.empty().then());
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationIdB)))
                .thenReturn(Mono.error(new RuntimeException("error deleteByAccountIdAndId location B")));
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationIdC)))
                .thenReturn(Mono.empty().then());

        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.eq(locationA)))
                .thenReturn(Mono.just(locationA));
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.eq(locationB)))
                .thenReturn(Mono.just(locationB));
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.eq(locationC)))
                .thenReturn(Mono.just(locationC));

        Set<String> locationIds = Set.of(locationIdA, locationIdB, locationIdC);
        StepVerifier.withVirtualTime(() -> sut.deleteAccountLocationsById(accountId, locationIds).log())
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(1))
                .then(() -> {
                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationIdA));
                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationIdA));
                    verify(locationRepository, times(1)).saveInactiveLocation(eq(locationA));

                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationIdB));
                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationIdB));
                    verify(locationRepository, times(0)).saveInactiveLocation(eq(locationB));

                    verify(locationRepository, times(1)).findByAccountIdAndId(eq(accountId), eq(locationIdC));
                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationIdC));
                    verify(locationRepository, times(1)).saveInactiveLocation(eq(locationC));
                })
                .verifyComplete();
    }
}
