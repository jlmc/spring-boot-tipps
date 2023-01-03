package io.github.jlmc.korders.processor.domain.model.aggregates;

import jakarta.persistence.AttributeConverter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FailureRecordCauseConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return new String(Base64.getEncoder().encode(attribute.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return new String(Base64.getDecoder().decode(dbData.getBytes(StandardCharsets.UTF_8)));
    }
}
