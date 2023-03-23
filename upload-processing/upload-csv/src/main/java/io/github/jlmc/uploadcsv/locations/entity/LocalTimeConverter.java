package io.github.jlmc.uploadcsv.locations.entity;

import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.regex.Pattern;

public class LocalTimeConverter {

    public static final LocalTimeConverter INSTANCE = new LocalTimeConverter();

    private static final Pattern HH_MM_P = Pattern.compile("^(1[0-2]|0?[1-9]):([0-5][0-9])\\s?(AM|PM|am|pm)$");
    private static final Pattern HH_P = Pattern.compile("^(1[0-2]|0?[1-9])(AM|PM|am|pm)$");

    private final DateTimeFormatter HH_MM_P_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("hh:mm")
            .appendText(ChronoField.AMPM_OF_DAY)
            .toFormatter();

    private final DateTimeFormatter HH_P_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("hh")
            .appendText(ChronoField.AMPM_OF_DAY)
            .toFormatter();

    public Object convertToRead(String value) throws CsvDataTypeMismatchException {
        return convert(value);
    }

    public LocalTime convert(String value) throws CsvDataTypeMismatchException {
        try {
            if (HH_MM_P.matcher(value).matches()) {
                return LocalTime.parse(value.toUpperCase(), HH_MM_P_FORMATTER);
            } else if (HH_P.matcher(value).matches()) {
                return LocalTime.parse(value.toUpperCase(), HH_P_FORMATTER);
            }
            return LocalTime.parse(value);
        } catch (Exception e) {
            throw new CsvDataTypeMismatchException("The value %s is not a valid local time".formatted(value));
        }
    }

    public String convertToWrite(LocalTime localTime) {
        if (localTime == null) return null;

        return localTime.format(HH_MM_P_FORMATTER);
    }
}
