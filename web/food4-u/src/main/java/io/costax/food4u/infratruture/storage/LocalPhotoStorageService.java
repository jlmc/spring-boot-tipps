package io.costax.food4u.infratruture.storage;

import io.costax.food4u.core.storage.StorageConfigurationProperties;
import io.costax.food4u.domain.services.PhotoStorageService;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LocalPhotoStorageService implements PhotoStorageService {

    private final StorageConfigurationProperties configuration;

    public LocalPhotoStorageService(final StorageConfigurationProperties configuration) {
        this.configuration = configuration;
    }

    @Override
    public StoragedPhoto getFile(final String fileName) {
        try {
            Path filePath = getFilePath(fileName);

            return StoragedPhoto.builder()
                    .inputStream(Files.newInputStream(filePath))
                    .build();

        } catch (IOException e) {
            //throw new UncheckedIOException(e);
            throw new StorageException("Cannot read back the file.", e);
        }
    }

    @Override
    public String storage(PhotoStream photoStream) {
        try {

            String fileName = PhotoStorageService.generateFileName(photoStream);

            Path destinationPath = getFilePath(fileName);

            FileCopyUtils.copy(photoStream.getInputStream(), Files.newOutputStream(destinationPath, StandardOpenOption.CREATE_NEW));

            return fileName;

        } catch (IOException e) {
            //throw new UncheckedIOException(e);
            throw new StorageException("Cannot store file.", e);
        }
    }

    @Override
    public void remove(final String fileName) {
        try {
            Path filePath = getFilePath(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // throw new UncheckedIOException(e);
            throw new StorageException("Cannot remove file.", e);
        }
    }

    private Path getFilePath(final String fileName) {
        return configuration.getLocal().getPath().resolve(Path.of(fileName));
    }

}
