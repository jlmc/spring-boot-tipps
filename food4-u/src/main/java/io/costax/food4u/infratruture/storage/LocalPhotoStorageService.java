package io.costax.food4u.infratruture.storage;

import io.costax.food4u.domain.services.PhotoStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
public class LocalPhotoStorageService implements PhotoStorageService {

    @Autowired
    PhotoStorageConfiguration configuration;

    @Override
    public InputStream getFile(final String fileName) {
        try {
            Path filePath = getFilePath(fileName);
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String storage(PhotoStream photoStream) {
        try {

            String fileName = generateFileName(photoStream);

            Path destinationPath = getFilePath(fileName);

            FileCopyUtils.copy(photoStream.getInputStream(), Files.newOutputStream(destinationPath, StandardOpenOption.CREATE_NEW));

            return fileName;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void remove(final String fileName) {
        try {
            Path filePath = getFilePath(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Path getFilePath(final String fileName) {
        return configuration.getLocalPath().resolve(Path.of(fileName));
    }

    private String generateFileName(final PhotoStream photoStream) {
        UUID uuid1 = UUID.randomUUID();
        return String.format("%s__%s", uuid1, photoStream.getName());
    }
}
