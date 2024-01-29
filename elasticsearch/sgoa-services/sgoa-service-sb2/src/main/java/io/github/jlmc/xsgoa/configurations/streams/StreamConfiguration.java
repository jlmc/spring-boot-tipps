package io.github.jlmc.xsgoa.configurations.streams;

import io.github.jlmc.xsgoa.infrastructure.bpm.consumer.BpmConsumer;
import io.github.jlmc.xsgoa.infrastructure.bpm.producers.BpmProducer;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(value = {
        BpmConsumer.class,
        BpmProducer.class
})
public class StreamConfiguration {
}
