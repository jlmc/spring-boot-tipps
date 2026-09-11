package io.github.jlmc.maydayhlsprovider.application.port.out;

import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import io.github.jlmc.maydayhlsprovider.domain.Encounter;
import io.github.jlmc.maydayhlsprovider.domain.TimeWindow;
import reactor.core.publisher.Flux;

public interface ProviderEncounterSearchPort {

    Flux<Encounter> search(AccountId accountId, TimeWindow window, String visitType);
}
