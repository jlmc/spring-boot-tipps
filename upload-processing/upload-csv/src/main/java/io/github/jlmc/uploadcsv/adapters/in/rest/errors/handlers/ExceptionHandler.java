package io.github.jlmc.uploadcsv.adapters.in.rest.errors.handlers;

import io.github.jlmc.uploadcsv.adapters.in.rest.errors.models.ApiError;

public interface ExceptionHandler<T extends Throwable> {

    ApiError handler(T exception, String code);
}
