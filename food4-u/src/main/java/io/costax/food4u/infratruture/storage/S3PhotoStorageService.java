package io.costax.food4u.infratruture.storage;

import com.amazonaws.SdkBaseException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.costax.food4u.core.storage.StorageConfigurationProperties;
import io.costax.food4u.domain.services.PhotoStorageService;

import java.net.URL;

public class S3PhotoStorageService implements PhotoStorageService {

    private final StorageConfigurationProperties storageConfigurationProperties;
    private final AmazonS3 amazonS3;

    public S3PhotoStorageService(final StorageConfigurationProperties storageConfigurationProperties, final AmazonS3 amazonS3) {
        this.storageConfigurationProperties = storageConfigurationProperties;
        this.amazonS3 = amazonS3;
    }

    @Override
    public StoragedPhoto getFile(final String fileName) {
        try {

            String filePath = getFilePath(fileName);

            URL url = amazonS3.getUrl(storageConfigurationProperties.getS3().getBucket(), filePath);

            return StoragedPhoto.builder()
                    .url(url.toString()).build();

        } catch (SdkBaseException e) {
            throw new StorageException("Cannot read file.", e);
        }
    }

    @Override
    public String storage(final PhotoStream photoStream) {
        try {
            String fileName = PhotoStorageService.generateFileName(photoStream);

            String filePath = getFilePath(fileName);

            var objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(photoStream.getContentType());

            var putObjectRequest = new PutObjectRequest(
                    storageConfigurationProperties.getS3().getBucket(),
                    filePath,
                    photoStream.getInputStream(),
                    objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest);

            return fileName;
        } catch (SdkBaseException e) {
            throw new StorageException("Cannot store file.", e);
        }
    }

    @Override
    public void remove(final String fileName) {
        try {
            String caminhoArquivo = getFilePath(fileName);

            var deleteObjectRequest = new DeleteObjectRequest(
                    storageConfigurationProperties.getS3().getBucket(), caminhoArquivo);

            amazonS3.deleteObject(deleteObjectRequest);

        } catch (SdkBaseException e) {
            throw new StorageException("Não foi possível excluir arquivo na Amazon S3.", e);
        }
    }

    private String getFilePath(String fileName) {
        return String.format("%s/%s", storageConfigurationProperties.getS3().getPhotosDirectory(), fileName);
    }
}
