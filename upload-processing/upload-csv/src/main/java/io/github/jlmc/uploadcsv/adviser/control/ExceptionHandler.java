package io.github.jlmc.uploadcsv.adviser.control;

import io.github.jlmc.uploadcsv.adviser.entity.ApiError;

public interface ExceptionHandler<T extends Throwable> {

    ApiError handler(T exception, String code);
}
