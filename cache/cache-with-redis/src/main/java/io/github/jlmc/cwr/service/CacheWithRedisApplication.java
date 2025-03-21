package io.github.jlmc.cwr.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CacheWithRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheWithRedisApplication.class, args);
    }

}
