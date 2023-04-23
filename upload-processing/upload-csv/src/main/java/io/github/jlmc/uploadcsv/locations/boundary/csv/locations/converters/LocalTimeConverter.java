package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoField.AMPM_OF_DAY;

public class LocalTimeConverter extends AbstractBeanField<LocalTime, String> {

    protected static final LocalTimeConverter INSTANCE = new LocalTimeConverter();

    private static final Pattern HH_MM_P = Pattern.compile("^(1[0-2]|0?\\d):([0-5]\\d)\\s?(AM|PM|am|pm)$");
    private static final Pattern HH_P = Pattern.compile("^(1[0-2]|0?\\d)(AM|PM|am|pm)$");

    private static final DateTimeFormatter HH_MM_P_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("hh:mm")
            .appendText(AMPM_OF_DAY)
            .toFormatter();
    private static final DateTimeFormatter HH_P_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("h")
            .appendText(AMPM_OF_DAY)
            .toFormatter();

    @Override
    public LocalTime convert(String value) throws CsvConstraintViolationException {
        try {
            if (HH_MM_P.matcher(value).matches()) {
                return LocalTime.parse(value.trim().toLowerCase(), HH_MM_P_FORMATTER);
            }

            if (HH_P.matcher(value).matches()) {
                return LocalTime.parse(value.trim().toLowerCase(), HH_P_FORMATTER);
            }

            return LocalTime.parse(value);

        } catch (DateTimeParseException e) {
            throw new CsvConstraintViolationException("The value " + value + " is not a valid local time.");
        }
    }

    public String convertToWrite(LocalTime localTime) {
        if (localTime == null) return null;

        return localTime.format(HH_MM_P_FORMATTER);
    }
}
