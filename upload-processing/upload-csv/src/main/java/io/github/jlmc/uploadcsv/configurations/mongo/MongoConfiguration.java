package io.github.jlmc.uploadcsv.configurations.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.List;

@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories
public class MongoConfiguration {

    /**
     * @see org.springframework.data.convert.Jsr310Converters
     */
    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.copyOf(CustomJsr310Converters.getConvertersToRegister()));
    }

    /**
     * @see <a href="https://github.com/mongock/mongock/discussions/615#discussioncomment-5465293">https://github.com/mongock/mongock/discussions/615#discussioncomment-5465293</a>
     */
    @Bean
    public ReactiveMongoTransactionManager transactionManager(ReactiveMongoDatabaseFactory mongoDatabaseFactory) {
        return new ReactiveMongoTransactionManager(mongoDatabaseFactory);
    }
}
