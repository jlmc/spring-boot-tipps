package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model.SlotCsvResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SlotCsvResourceListConverter extends AbstractBeanField<List<SlotCsvResource>, String> {

    private static final String SEPARATOR_REGEX = "\\|";
    private static final String SLOT_PERIOD_SEPARATOR = "-";
    private final LocalTimeConverter LOCAL_TIME_CONVERTER = LocalTimeConverter.INSTANCE;

    @Override
    protected Object convert(String value) throws CsvConstraintViolationException {
        if (value == null || value.trim().isBlank()) {
            return Collections.emptyList();
        }

        List<String> periods =
                Arrays.stream(value.trim().split(SEPARATOR_REGEX))
                      .map(String::trim)
                      .toList();

        List<SlotCsvResource> slots = new ArrayList<>();

        for (String period : periods) {
            SlotCsvResource slot = toSlot(period);
            if (slot != null) {
                slots.add(slot);
            }
        }

        return slots;
    }

    private SlotCsvResource toSlot(String period) throws CsvConstraintViolationException {
        String[] splits = period.trim().split(SLOT_PERIOD_SEPARATOR);

        if (splits.length == 0) return null;

        if (splits.length > 2) {
            throw new CsvConstraintViolationException(
                    "The csv slots periods must have a start and a close element, the value " + period + " can be parsed to a Slot.");
        }

        return new SlotCsvResource(LOCAL_TIME_CONVERTER.convert(splits[0]), LOCAL_TIME_CONVERTER.convert(splits[1]));
    }
}
