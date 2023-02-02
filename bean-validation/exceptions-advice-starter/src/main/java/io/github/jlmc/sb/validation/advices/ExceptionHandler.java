package io.github.jlmc.sb.validation.advices;

import io.github.jlmc.sb.validation.errors.ApiError;

public interface ExceptionHandler {

    ApiError handler(Throwable throwable, String errorCode);
}
