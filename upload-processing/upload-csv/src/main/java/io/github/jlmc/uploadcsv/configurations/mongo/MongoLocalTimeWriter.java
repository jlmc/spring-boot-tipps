package io.github.jlmc.uploadcsv.configurations.mongo;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalTime;

@org.springframework.stereotype.Component
@WritingConverter
public class MongoLocalTimeWriter implements Converter<LocalTime, String> {
    @Override
    public String convert(LocalTime source) {
        return source.toString();
    }
}
// MongoOffsetDateTimeWriter
