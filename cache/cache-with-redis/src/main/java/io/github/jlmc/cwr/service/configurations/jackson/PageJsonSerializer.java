package io.github.jlmc.cwr.service.configurations.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

import java.io.IOException;

/**
 * The default Page json serialization contains to many and some duplicated information.
 * This implementation intend to eliminate some duplicate information and only serialize the important information.
 */
@JsonComponent
public class PageJsonSerializer extends JsonSerializer<Page<?>> {

    @Override
    public void serialize(final Page<?> page, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {

        gen.writeStartObject();

        gen.writeObjectField("content", page.getContent());
        gen.writeNumberField("size", page.getSize());
        gen.writeNumberField("totalElements", page.getTotalElements());
        gen.writeNumberField("totalPages", page.getTotalPages());
        gen.writeNumberField("number", page.getNumber());

        gen.writeEndObject();

    }
}
