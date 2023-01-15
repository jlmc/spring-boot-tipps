package io.github.jlmc.sbvalidation;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

public class Resources {

    public static String classPathResourceContent(String resource) {
        //Resource resource = resourceLoader.getResource("classpath:response/movies-ok-response.json");
        ClassPathResource classPathResource = new ClassPathResource(resource);
        return resourceContent(classPathResource);
    }

    public static String resourceContent(Resource resource) {
        try {
            return StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
