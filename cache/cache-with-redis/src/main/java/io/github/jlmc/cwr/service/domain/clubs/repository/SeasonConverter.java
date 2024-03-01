package io.github.jlmc.cwr.service.domain.clubs.repository;


import io.github.jlmc.cwr.service.domain.clubs.entities.Season;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SeasonConverter implements AttributeConverter<Season, String> {
    @Override
    public String convertToDatabaseColumn(Season attribute) {
        if (attribute == null) return null;
        return attribute.title();
    }

    @Override
    public Season convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return new Season(dbData);
    }
}
