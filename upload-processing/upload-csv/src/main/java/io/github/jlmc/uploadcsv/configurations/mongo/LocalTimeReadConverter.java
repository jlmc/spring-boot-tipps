package io.github.jlmc.uploadcsv.configurations.mongo;

import org.springframework.data.convert.ReadingConverter;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@org.springframework.stereotype.Component
@ReadingConverter
public class LocalTimeReadConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String source) {
        try {
            return LocalTime.parse(source);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
    }
}
