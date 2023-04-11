package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model.SlotCsvResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SlotCsvResourceListConverter extends AbstractBeanField<List<SlotCsvResource>, String> {

    public static final String SEPARATOR = "|";
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


    @Override
    protected String convertToWrite(Object value) {
        if (value == null) return null;

        StringBuilder stringBuilder = new StringBuilder();
        List<SlotCsvResource> slots = (List<SlotCsvResource>) value;
        for (SlotCsvResource slot : slots) {
            String start = Optional.of(slot)
                                   .map(SlotCsvResource::getOpenAt)
                                   .map(LOCAL_TIME_CONVERTER::convertToWrite)
                                   .orElse(null);
            String end = Optional.of(slot)
                                 .map(SlotCsvResource::getCloseAt)
                                 .map(LOCAL_TIME_CONVERTER::convertToWrite)
                                 .orElse(null);

            if (start != null) {
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append(SEPARATOR);
                }

                stringBuilder.append(start);
                if (end != null) {
                    stringBuilder.append(SLOT_PERIOD_SEPARATOR).append(end);
                }
            }
        }

        return stringBuilder.toString();
    }

    private SlotCsvResource toSlot(String period) throws CsvConstraintViolationException {
        String[] splits = period.trim().split(SLOT_PERIOD_SEPARATOR);

        if (splits.length == 0) return null;

        if (splits.length != 2) {
            throw new CsvConstraintViolationException(
                    "The csv slots periods must have a start and a close element, the value " + period + " can be parsed to a Slot.");
        }

        return new SlotCsvResource(LOCAL_TIME_CONVERTER.convert(splits[0]), LOCAL_TIME_CONVERTER.convert(splits[1]));
    }
}