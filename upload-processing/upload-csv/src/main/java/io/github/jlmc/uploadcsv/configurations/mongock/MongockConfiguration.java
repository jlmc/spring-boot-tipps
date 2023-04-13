package io.github.jlmc.uploadcsv.configurations.mongock;

import com.mongodb.reactivestreams.client.MongoClient;
import io.github.jlmc.uploadcsv.migrations.UpdateLocationsOpenAndCloseAtToIsoLocalTime;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.springboot.EnableMongock;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Please read the official documentation, and the GitHub examples repository.
 *
 * @see io.mongock.runner.springboot.config.MongockContext
 * @see <a href="https://github.com/mongock/mongock-examples">https://github.com/mongock/mongock-exampleso</a>
 * @see <a href="https://docs.mongock.io/v5/driver/mongodb-reactive/index.html">https://docs.mongock.io/v5/driver/mongodb-reactive/index.html</a>
 */
@EnableMongock
@Configuration
@ConditionalOnExpression("${mongock.enabled:true}")
public class MongockConfiguration {

    @Bean
    public io.mongock.driver.api.driver.ConnectionDriver mongockDriver(MongoClient reactiveMongoClient, MongoProperties mongoProperties) {
        String mongoClientDatabase = mongoProperties.getMongoClientDatabase();
        return MongoReactiveDriver.withDefaultLock(reactiveMongoClient, mongoClientDatabase);
    }

    //@Bean
    @SuppressWarnings("unused")
    public MongockInitializingBeanRunner getBuilder(MongoClient reactiveMongoClient,
                                                    ApplicationContext context,
                                                    MongoProperties mongoProperties) {

        Package aPackage = UpdateLocationsOpenAndCloseAtToIsoLocalTime.class.getPackage();
        String name = aPackage.getName();
        MongoReactiveDriver connectionDriver =
                MongoReactiveDriver.withDefaultLock(reactiveMongoClient, mongoProperties.getMongoClientDatabase());
        return MongockSpringboot.builder()
                                .setDriver(connectionDriver)
                                .addMigrationScanPackage(UpdateLocationsOpenAndCloseAtToIsoLocalTime.class.getPackage().getName())
                                .setSpringContext(context)
                                .setTransactionEnabled(false)
                                //.setTransactionEnabled(true)
                                .buildInitializingBeanRunner();
    }
}
