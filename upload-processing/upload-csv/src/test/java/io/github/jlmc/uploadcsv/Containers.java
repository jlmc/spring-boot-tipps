package io.github.jlmc.uploadcsv;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.MongoDBContainer;

public class Containers {

    @NotNull
    public static MongoDBContainer getMongoDBContainer() {
        return new MongoDBContainer("mongo:5.0");
    }
}
