package io.github.jlmc.uof.domain.fruits.core;

import io.github.jlmc.uof.domain.commons.NoFoundException;
import io.github.jlmc.uof.domain.fruits.entities.Fruit;
import io.github.jlmc.uof.domain.fruits.entities.FruitFullDetailed;
import io.github.jlmc.uof.domain.fruits.entities.PriceDetail;
import io.github.jlmc.uof.domain.fruits.ports.incoming.FruitFullDetailedReader;
import io.github.jlmc.uof.domain.fruits.ports.outgoing.FruitsRepository;
import io.github.jlmc.uof.domain.fruits.ports.outgoing.MarketProductInformationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FruitFullDetailedReaderService implements FruitFullDetailedReader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FruitFullDetailedReaderService.class);

    private final FruitsRepository fruitsRepository;
    private final MarketProductInformationProvider marketProductInformationProvider;

    public FruitFullDetailedReaderService(FruitsRepository fruitsRepository,
                                          MarketProductInformationProvider marketProductInformationProvider) {
        this.fruitsRepository = fruitsRepository;
        this.marketProductInformationProvider = marketProductInformationProvider;
    }

    @Override
    public Optional<FruitFullDetailed> find(String productId) {
        LOGGER.info("Find the full fruit information of the productId: {}.", productId);

        Fruit fruit =
                fruitsRepository.findById(productId)
                        .orElseThrow(() -> new NoFoundException("The fruit with productId %s not in the repository.".formatted(productId)));

        LOGGER.debug("Found the fruit in the repository with the productId: {}.", productId);

        LOGGER.debug("Search for the best price in the market for the product productId {}.", productId);
        PriceDetail productPrice =
                marketProductInformationProvider.getProductPrice(productId);
        LOGGER.debug("Found a best price in the market for the product productId {}.", productId);

        return Optional.of(new FruitFullDetailed(fruit, productPrice));
    }
}
