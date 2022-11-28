package io.costax.food4u.core.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConfigurationProperties("food4u.storage")
public class StorageConfigurationProperties {

    @Getter
    @Setter
    private Local local = new Local();

    @Getter
    @Setter
    private S3 s3 = new S3();

    @Getter
    @Setter
    private StorageType type = StorageType.LOCAL;

    @Getter
    @Setter
    public static class Local {

        private Path path = Path.of("/Users/costax/Documents/junk/costax/springs/spring-boot-rest-notes/.catalog");

    }

    @Getter
    @Setter
    public static class S3 {

        private String accessKeyId;
        private String accessKeySecret;
        private String bucket;
        private String region;
        private String photosDirectory;

    }

    public enum StorageType {
        LOCAL, S3
    }
}
