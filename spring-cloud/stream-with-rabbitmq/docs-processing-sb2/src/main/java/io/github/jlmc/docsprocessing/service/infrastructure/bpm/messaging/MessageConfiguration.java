package io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging;

import io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.consumer.BpmConsumer;
import io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.producer.BpmProducer;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding({BpmConsumer.class, BpmProducer.class})
public class MessageConfiguration {
}
