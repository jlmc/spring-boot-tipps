package io.github.jlmc.uploadcsv.configurations.mongo;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @see org.springframework.data.convert.Jsr310Converters
 */
final class CustomJsr310Converters {

    private CustomJsr310Converters() {
    }

    public static Collection<Converter<?, ?>> getConvertersToRegister() {
        List<Converter<?, ?>> converters = new ArrayList<>();

        converters.add(LocalTimeReaderConverter.INSTANCE);
        converters.add(LocalTimeWriterConverter.INSTANCE);


        converters.add(ZonedDateTimeReaderConverter.INSTANCE);
        converters.add(ZonedDateTimeWriterConverter.INSTANCE);

        converters.add(LocalDateReaderConverter.INSTANCE);
        converters.add(LocalDateWriterConverter.INSTANCE);

        return converters;
    }

    @ReadingConverter
    public enum LocalDateReaderConverter implements Converter<String, LocalDate> {
        INSTANCE;

        @Override
        public LocalDate convert(@NonNull String source) {
            try {
                return LocalDate.parse(source);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    @WritingConverter
    public enum LocalDateWriterConverter implements Converter<LocalDate, String> {
        INSTANCE;

        @NonNull
        @Override
        public String convert(LocalDate source) {
            return source.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    @ReadingConverter
    public enum LocalTimeReaderConverter implements Converter<String, LocalTime> {
        INSTANCE;

        @NonNull
        @Override
        public LocalTime convert(@NonNull String source) {
            try {
                return LocalTime.parse(source);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    @WritingConverter
    public enum LocalTimeWriterConverter implements Converter<LocalTime, String> {
        INSTANCE;

        @NonNull
        @Override
        public String convert(LocalTime source) {
            return source.format(DateTimeFormatter.ISO_LOCAL_TIME);
        }
    }

    @ReadingConverter
    public enum ZonedDateTimeReaderConverter implements Converter<java.util.Date, ZonedDateTime> {
        INSTANCE;

        @NonNull
        @Override
        public ZonedDateTime convert(java.util.Date source) {
            return source.toInstant().atZone(ZoneOffset.UTC);
        }
    }

    @WritingConverter
    public enum ZonedDateTimeWriterConverter implements Converter<ZonedDateTime, java.util.Date> {
        INSTANCE;

        @NonNull
        @Override
        public Date convert(ZonedDateTime source) {
            return Date.from(source.toInstant());
        }
    }

}
