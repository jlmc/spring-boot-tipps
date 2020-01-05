package io.costax.food4u.core.storage;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.costax.food4u.domain.services.PhotoStorageService;
import io.costax.food4u.infratruture.storage.LocalPhotoStorageService;
import io.costax.food4u.infratruture.storage.S3PhotoStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @see ConditionalOnProperty
 * @see <a href="https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/condition/ConditionalOnProperty.html">ConditionalOnProperty official dcumentation</a>
 */
@Configuration
public class StorageConfig {

    @Bean
    @ConditionalOnProperty(name = "food4u.storage.type", havingValue = "s3")
    AmazonS3 amazonS3(StorageConfigurationProperties storageConfigurationProperties) {

        final StorageConfigurationProperties.S3 s3 = storageConfigurationProperties.getS3();

        var credentials = new BasicAWSCredentials(
                s3.getAccessKeyId(),
                s3.getAccessKeySecret());

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(s3.getRegion())
                .build();
    }

    @Bean
    PhotoStorageService photoStorageService(StorageConfigurationProperties storageConfigurationProperties) {

        if (storageConfigurationProperties.getType() == null) {
            throw new IllegalStateException("The application property 'food4u.storage.type' should not be null");
        }

        return switch (storageConfigurationProperties.getType()) {
            case LOCAL -> new LocalPhotoStorageService(storageConfigurationProperties);
            case S3 -> new S3PhotoStorageService(storageConfigurationProperties, amazonS3(storageConfigurationProperties));
            default -> throw new IllegalArgumentException();
        };

    }


}
