package io.github.jlmc.reactive.domain.services.prefixes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class PrefixesInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrefixesInitializer.class);

    private final PrefixesService prefixesService;

    public PrefixesInitializer(PrefixesService prefixesService) {
        this.prefixesService = prefixesService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {

            LOGGER.info("Initializing 'prefixes' service...");

            prefixesService.loadPrefixes();
        } catch (Exception e) {
            LOGGER.info("Unexpected problem happens on 'prefixes' service initialization!");
            throw new IllegalStateException(e);
        }
    }
}
