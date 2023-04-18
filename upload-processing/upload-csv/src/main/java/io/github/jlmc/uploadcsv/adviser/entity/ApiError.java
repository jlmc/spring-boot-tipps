package io.github.jlmc.uploadcsv.adviser.entity;

import java.util.List;

public record ApiError(
        String code,
        String message,
        List<ErrorField> fields

) {
}
