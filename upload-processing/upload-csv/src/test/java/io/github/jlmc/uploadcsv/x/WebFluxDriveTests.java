package io.github.jlmc.uploadcsv.x;

import io.github.jlmc.uploadcsv.locations.entity.Location;
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
public class WebFluxDriveTests {

    @Mock
    private LRepository locationRepository;

    @InjectMocks
    private DeleteAccountLocationByIdInteractor sut;

    @Test
    void whenAllMethodsRunsOk() {
        String accountId = "ACCOUNT_ID";
        String locationId = "mockLocationId";
        Location location = Location.builder().id(locationId).name("tests instance").build();

        lenient()
                .when(locationRepository.getLocationByAccountIdAndId(eq(accountId), eq(locationId)))
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
                    verify(locationRepository, times(1)).getLocationByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, times(1)).saveInactiveLocation(any());
                })
                .verifyComplete();
    }

    @Test
    void whenFindByIdReturnError() {
        String accountId = "ACCOUNT_ID";
        String locationId = "mockLocationId";
        Location location = Location.builder().id(locationId).name("tests instance").build();

        lenient()
                .when(locationRepository.getLocationByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.error(new RuntimeException("--->Some expected error <getLocationByAccountIdAndId> !!! <---")));
        lenient()
                .when(locationRepository.deleteByAccountIdAndId(eq(accountId), eq(locationId)))
                .thenReturn(Mono.error(new RuntimeException("--->Some expected error <deleteByAccountIdAndId>!!! <---")));
        lenient()
                .when(locationRepository.saveInactiveLocation(Mockito.any(Location.class)))
                .thenReturn(Mono.just(location));

        StepVerifier.withVirtualTime(() -> sut.deleteAccountLocationsById(accountId, Set.of(locationId)).log())
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(1))
                .then(() -> {
                    verify(locationRepository, times(1)).getLocationByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, never()).deleteByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, never()).saveInactiveLocation(any());
                })
                .verifyComplete();
    }

    @Test
    void whenDeleteByIdReturnsError() {
        String accountId = "ACCOUNT_ID";
        String locationId = "mockLocationId";
        Location location = Location.builder().id(locationId).name("tests instance").build();

        lenient()
                .when(locationRepository.getLocationByAccountIdAndId(eq(accountId), eq(locationId)))
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
                    verify(locationRepository, times(1)).getLocationByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, never()).saveInactiveLocation(any());
                })
                .verifyComplete();


    }


    @Test
    void whenSaveInactiveReturnsError() {
        String accountId = "ACCOUNT_ID";
        String locationId = "mockLocationId";
        Location location = Location.builder().id(locationId).name("tests instance").build();

        lenient()
                .when(locationRepository.getLocationByAccountIdAndId(eq(accountId), eq(locationId)))
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
                    verify(locationRepository, times(1)).getLocationByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, times(1)).deleteByAccountIdAndId(eq(accountId), eq(locationId));

                    verify(locationRepository, times(1)).saveInactiveLocation(any());
                })
                .verifyComplete();
    }

    ////



}
