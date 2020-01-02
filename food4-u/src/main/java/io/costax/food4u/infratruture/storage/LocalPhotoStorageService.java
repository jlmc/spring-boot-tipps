package io.costax.food4u.infratruture.storage;

import io.costax.food4u.domain.services.PhotoStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
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
    public String storage(PhotoStream photoStream) {
        try {

            Path destinationPath = getDestinationPath(photoStream);

            FileCopyUtils.copy(photoStream.getInputStream(), Files.newOutputStream(destinationPath, StandardOpenOption.CREATE_NEW));
            String s = destinationPath.toString();
            return s;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Path getDestinationPath(final PhotoStream photoStream) {
        UUID uuid1 = UUID.randomUUID();
        Path of = Path.of(uuid1.toString() + "__" + photoStream.getName());
        Path resolve = configuration.getLocalPath().resolve(of);
        return resolve;
    }
}
