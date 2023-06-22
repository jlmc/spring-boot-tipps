package io.github.jlmc.uploadcsv.adapters.in.rest.csv.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;

import java.time.ZoneId;

public class CsvZoneIdConverter extends AbstractBeanField<ZoneId, String> {
    @Override
    protected Object convert(String value) throws CsvConstraintViolationException {
        return zoneIdOf(value);
    }

    public static ZoneId zoneIdOf(String value) throws CsvConstraintViolationException {
        if (value == null) return null;

        if (!ZoneId.getAvailableZoneIds().contains(value))
            throw new CsvConstraintViolationException("'%s' is not in the zones list defined by IANA.".formatted(value));

        return ZoneId.of(value);
    }
}
