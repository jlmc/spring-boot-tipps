package io.costax.food4u.domain.services;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

public interface PhotoStorageService {

    String storage(PhotoStream photoStream);

    @Builder
    @Getter
    class PhotoStream {
        private String name;
        private InputStream inputStream;
    }
}
