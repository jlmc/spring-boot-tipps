package io.github.jlmc.uploadcsv;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Objects;

public class BodyFactory {
    public static BodyInserter<?, ? super ClientHttpRequest> fileMultipart(Resource resource) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", resource)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .filename(Objects.requireNonNull(resource.getFilename()));

        return BodyInserters.fromMultipartData(multipartBodyBuilder.build());
    }
}
