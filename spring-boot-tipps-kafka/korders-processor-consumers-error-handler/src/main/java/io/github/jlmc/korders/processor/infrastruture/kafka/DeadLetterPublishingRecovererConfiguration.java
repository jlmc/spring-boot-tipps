package io.github.jlmc.korders.processor.infrastruture.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

@Configuration
public class DeadLetterPublishingRecovererConfiguration {

    @Value("${x.topics.retry}")
    String retryTopic;
    @Value("${x.topics.dtl}")
    String dtlTopic;

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            KafkaTemplate kafkaTemplate
    ) {

        var deadLetterPublishingRecovererDestinantionResolver =
                new DeadLetterPublishingRecovererDestinantionResolver(retryTopic, dtlTopic);
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer =
                new DeadLetterPublishingRecoverer(
                    kafkaTemplate,
                        deadLetterPublishingRecovererDestinantionResolver
                );

        return deadLetterPublishingRecoverer;
    }
}
