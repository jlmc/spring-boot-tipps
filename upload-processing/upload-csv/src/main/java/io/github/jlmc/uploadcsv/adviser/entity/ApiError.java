package io.github.jlmc.uploadcsv.adviser.entity;

import java.util.Collections;
import java.util.List;

public record ApiError(
        String code,
        String message,
        List<ErrorField> fields

) {
    public ApiError(String code, String message) {
        this(code, message, Collections.emptyList());
    }
}
