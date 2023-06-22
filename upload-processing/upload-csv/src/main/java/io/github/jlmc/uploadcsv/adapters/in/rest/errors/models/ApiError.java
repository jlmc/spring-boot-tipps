package io.github.jlmc.uploadcsv.adapters.in.rest.errors.models;

import java.util.List;

public record ApiError(
        String code,
        String message,
        List<ErrorField> fields

) {
}
