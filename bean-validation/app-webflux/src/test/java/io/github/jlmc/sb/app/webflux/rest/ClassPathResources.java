package io.github.jlmc.sb.app.webflux.rest;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ClassPathResources {

    public static byte[] fromFile(String path) {
        try {
            return new ClassPathResource(path).getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
