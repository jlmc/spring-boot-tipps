package io.github.jlmc.uploadcsv.configurations.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
//@EnableReactiveMongoAuditing
public class MongoConfiguration {


    /*
        @Bean
    fun mongoCustomConversions(): MongoCustomConversions? {
        return MongoCustomConversions(
            listOf(
                MongoOffsetDateTimeWriter(),
                MongoOffsetDateTimeReader()
            )
        )
    }
     */
    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(
                List.of(
                        new MongoLocalTimeReader(),
                        new MongoLocalTimeWriter()
                        )
        );
    }
}
