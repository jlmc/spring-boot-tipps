package io.github.jlmc.cwr.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Component
public class ExpectedResources {

    @Autowired
    ResourceLoader resourceLoader;

    public String transfersPage0() {
        return contentOf("classpath:asserts-responses/transfers/transfers-page-0.json");
    }

    public String transfersPage1() {
        return contentOf("classpath:asserts-responses/transfers/transfers-page-1.json");
    }

    public String transfersPage9() {
        return contentOf("classpath:asserts-responses/transfers/transfers-page-9.json");
    }

    private String contentOf(String location) {
        try {
            Resource resource = resourceLoader.getResource(location);

            File file = resource.getFile();

            return Files.readString(file.toPath(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
