package io.github.jlmc.uploadcsv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

//@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories
@SpringBootApplication
//@SpringBootApplication(exclude = {MongoAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class})
public class UploadCsvApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadCsvApplication.class, args);
    }

}
