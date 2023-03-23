package io.github.jlmc.uploadcsv.slots.control;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class CsvTemplateWriterService {

    public Mono<InputStream> generateCsv() {
        return Mono.fromCallable(() -> {
            Resource resource = new ClassPathResource("csv/template.csv");
            return new FileInputStream(resource.getFile());
        });
    }
}
