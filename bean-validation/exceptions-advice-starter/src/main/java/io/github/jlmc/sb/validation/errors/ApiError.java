package io.github.jlmc.sb.validation.errors;

import java.util.List;

public record ApiError(
        int httpCode,
        String code,
        String message,
        List<ErrorField> fields
) {
}
