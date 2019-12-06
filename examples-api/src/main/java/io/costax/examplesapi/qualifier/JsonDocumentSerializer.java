package io.costax.examplesapi.qualifier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Qualifier("normal")
@Component
public class JsonDocumentSerializer implements DocumentSerializer {

    @Override
    public String serialize(final String id) {
//        return """
//                {
//                    "id": \"""" + String.format("%s", id) +
//                """
//                }
//                """;
        return """
                {
                    "id": "${id}"
                }
                """.replace("${id}", id);
    }

    @Override
    public String contentType() {
        return "application/json";
    }
}
