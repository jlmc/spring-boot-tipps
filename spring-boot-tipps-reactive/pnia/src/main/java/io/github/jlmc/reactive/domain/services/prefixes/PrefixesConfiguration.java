package io.github.jlmc.reactive.domain.services.prefixes;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Validated
@Component
@ConfigurationProperties(prefix = "pnia.prefixes")
public class PrefixesConfiguration {

    @NotNull
    @NotBlank
    private String filePath;
    @NotNull
    private PathType pathType = PathType.CLASSPATH;

    public enum PathType {
        CLASSPATH {
            @Override
            public List<String> readLines(String filePath) throws IOException {
                try (
                        InputStream is = PrefixesService.class.getResourceAsStream(filePath);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    return reader.lines().collect(Collectors.toList());
                }
            }
        },
        SYSTEM {
            @Override
            public List<String> readLines(String filePath) throws IOException {
                return Files.readAllLines(Path.of(filePath));
            }
        };

        public abstract List<String> readLines(String filePath) throws IOException;
    }
}
