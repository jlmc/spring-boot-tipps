package io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.consumer;

import io.github.jlmc.docsprocessing.service.commons.gateway.response.ExternalTaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExternalTaskCrcValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalTaskCrcValidator.class);

    public void accept(ExternalTaskDto externalTaskDto) {

        LOGGER.info("--> {}", externalTaskDto);

    }
}
