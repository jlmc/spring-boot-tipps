package io.costax.food4u.domain.services;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;
import java.util.UUID;

public interface PhotoStorageService {

    StoragedPhoto getFile(String fileName);

    String storage(PhotoStream photoStream);

    void remove(String fileName);

    default String replace(String fileName, PhotoStream photoStream) {
        if (fileName != null) {
            remove(fileName);
        }

        return storage(photoStream);
    }

    @Builder
    @Getter
    class PhotoStream {
        private String name;
        private InputStream inputStream;
        private String contentType;
    }

    @Builder
    @Getter
    class StoragedPhoto {
        private InputStream inputStream;
        private String url;
    }

    static String generateFileName(final PhotoStream photoStream) {
        UUID uuid1 = UUID.randomUUID();
        return String.format("%s__%s", uuid1, photoStream.getName());
    }
}
