package io.github.jlmc.uploadcsv.locations.entity;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SlotsConverter extends AbstractBeanField<List<Slot>, String> {
    public static final String SEPARATOR = "|";
    public static final String SEPARATOR_REGEX = "\\|";
    public static final String SLOT_PERIOD_SEPARATOR = "-";
    private final LocalTimeConverter localTimeConverter;

    public SlotsConverter() {
        this.localTimeConverter = LocalTimeConverter.INSTANCE;
    }

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException {
        if (StringUtils.isBlank(value)) {
            return List.of();
        }
        String trimmedString = value.trim();

        String[] split = trimmedString.split(SEPARATOR_REGEX);

        List<Slot> openHours = new ArrayList<>();
        for (String period : split) {
            String[] split1 = period.trim().split(SLOT_PERIOD_SEPARATOR);
            if (split1.length > 0) {
                String s = split1[0];
                LocalTime start = localTimeConverter.convert(split1[0]);
                LocalTime end = split1.length > 1 ? localTimeConverter.convert(split1[1]) : null;
                Slot slot = Slot.of(start, end);
                openHours.add(slot);
            }
        }

        return openHours;
    }


    @Override
    protected String convertToWrite(Object value) {
        if (value == null) return null;

        StringBuilder stringBuilder = new StringBuilder();
        List<Slot> slots = (List<Slot>) value;
        for (Slot slot : slots) {
            String start = Optional.of(slot)
                               .map(Slot::getOpenAt)
                               .map(localTimeConverter::convertToWrite)
                               .orElse(null);
            String end = Optional.of(slot)
                                   .map(Slot::getCloseAt)
                                   .map(localTimeConverter::convertToWrite)
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
}
