package io.github.jlmc.uploadcsv;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

public class Resources {

    public static Resource classPathResource(String path) {
        return new ClassPathResource(path);
    }

    public static String classPathResourceContent(String path) {
        return resourceContent(new ClassPathResource(path));
    }

    public static String resourceContent(Resource resource) {
        try {
            return Files.readString(resource.getFile().toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
