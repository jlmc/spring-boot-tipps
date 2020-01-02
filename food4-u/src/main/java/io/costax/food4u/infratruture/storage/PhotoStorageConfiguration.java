package io.costax.food4u.infratruture.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConfigurationProperties("food4u.photo-storage")
public class PhotoStorageConfiguration {

    private Path localPath = Path.of("/Users/costax/Documents/junk/projects/spring-notes/spring-boot-rest-notes/.catalog");

    public Path getLocalPath() {
        return localPath;
    }

    public void setLocalPath(final Path localPath) {
        this.localPath = localPath;
    }
}
