package io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.consumer;

import io.github.jlmc.docsprocessing.service.commons.gateway.response.ExternalTaskDto;
import io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.DockerImages;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestChannelBinderConfiguration.class)
class ExternalTaskCrcValidatorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalTaskCrcValidatorTest.class);

    @Container
    private static final RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer(DockerImages.RABBIT_MQ);

    @SpyBean
    ExternalTaskCrcValidator validator;

    @Autowired
    StreamBridge publisher;

    @Autowired
    private InputDestination inputDestination;

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        LOGGER.info("Overriding the default properties with test container!");
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.datasource.host", rabbitMQContainer::getHost);
        registry.add("spring.datasource.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.datasource.password", rabbitMQContainer::getAdminPassword);
    }

    @Test
    void when_receive_messages_in_the_documentProcessing_crc_validation_queue_the_validator_should_be_called() {
        ExternalTaskDto externalTaskDto = new ExternalTaskDto("abc-1");

        Message<ExternalTaskDto> message = MessageBuilder
                .withPayload(externalTaskDto)
                // define the routing key
                .setHeader("spring.cloud.stream.sendto.destination", "documentProcessing_crc_validation")
                .build();

        inputDestination.send(message, "bpm.notify.externaltask");

        //Thread.sleep(3000L);

        verify(validator, times(1)).accept(eq(externalTaskDto));
    }
}