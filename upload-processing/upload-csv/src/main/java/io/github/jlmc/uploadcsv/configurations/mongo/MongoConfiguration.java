package io.github.jlmc.uploadcsv.configurations.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
//@EnableReactiveMongoAuditing
public class MongoConfiguration {


    /**
     * @see org.springframework.data.convert.Jsr310Converters
     */
    @org.springframework.context.annotation.Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.copyOf(CustomJsr310Converters.getConvertersToRegister()));
    }
}
