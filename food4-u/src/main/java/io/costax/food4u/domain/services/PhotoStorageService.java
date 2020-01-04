package io.costax.food4u.domain.services;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

public interface PhotoStorageService {

    InputStream getFile(String fileName);

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
    }
}
