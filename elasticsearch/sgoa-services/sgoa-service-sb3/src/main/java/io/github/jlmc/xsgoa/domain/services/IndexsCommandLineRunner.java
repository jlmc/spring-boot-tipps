package io.github.jlmc.xsgoa.domain.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor

@Service
@ConditionalOnProperty(name = "app.dev.populator.enabled", havingValue = "true")
public class IndexsCommandLineRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexsCommandLineRunner.class);

    private final CustomerCostumesIndexPopularService customerCostumesIndexPopularService;

    @Override
    @Transactional
    public void run(String... args) {
        LOGGER.info("Populating the customer index");
        customerCostumesIndexPopularService.populate();
        LOGGER.info("Populated the customer index successful");

    }
}
