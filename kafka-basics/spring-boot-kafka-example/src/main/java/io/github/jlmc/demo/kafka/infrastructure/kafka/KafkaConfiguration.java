package io.github.jlmc.demo.kafka.infrastructure.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class KafkaConfiguration {
    public static final String BILLING = "localhost:5152";
    public static final String ORDER = "localhost:6162";
}
