package io.github.jlmc.uploadcsv.adviser.boundary;

import io.github.jlmc.uploadcsv.adviser.control.ConstraintViolationExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.control.CsvConstraintViolationsExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.control.MethodArgumentNotValidExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.control.WebExchangeBindExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.entity.ApiError;
import io.github.jlmc.uploadcsv.csv.boundary.CsvConstraintViolationsException;
import io.github.jlmc.uploadcsv.csv.boundary.CsvIllegalDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvice {

    @Autowired
    private ConstraintViolationExceptionHandler constraintViolationExceptionHandler;
    @Autowired
    private WebExchangeBindExceptionHandler webExchangeBindExceptionHandler;
    @Autowired
    private MethodArgumentNotValidExceptionHandler methodArgumentNotValidExceptionHandler;
    @Autowired
    private CsvConstraintViolationsExceptionHandler csvConstraintViolationsExceptionHandler;
    @Autowired
    private CsvIllegalDataExceptionHandler csvIllegalDataExceptionHandler;
    @Autowired
    private ApiErrorsConfigurationProperties apiErrorsProperties;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> constraintViolationExceptionHandler(ConstraintViolationException exception) {
        ApiError apiError = constraintViolationExceptionHandler.handler(exception, apiErrorsProperties.getCode() + "4000");
        return ResponseEntity.status(400).body(apiError);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiError> webExchangeBindExceptionHandler(WebExchangeBindException exception) {
        ApiError apiError = methodArgumentNotValidExceptionHandler.handler(exception, apiErrorsProperties.getCode() + "4000");
        return ResponseEntity.status(400).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {
        ApiError apiError = webExchangeBindExceptionHandler.handler(exception, apiErrorsProperties.getCode() + "4000");
        return ResponseEntity.status(400).body(apiError);
    }

    @ExceptionHandler(CsvConstraintViolationsException.class)
    public ResponseEntity<ApiError> csvConstraintViolationsExceptionHandler(CsvConstraintViolationsException exception) {
        return ResponseEntity.status(400).body(csvConstraintViolationsExceptionHandler.handler(exception, csvBadRequestCode()));
    }

    @ExceptionHandler(CsvIllegalDataException.class)
    public ResponseEntity<ApiError> csvIllegalDataExceptionHandler(CsvIllegalDataException exception) {
        return ResponseEntity.status(400).body(csvIllegalDataExceptionHandler.handler(exception, csvBadRequestCode()));
    }

    private String csvBadRequestCode() {
        return apiErrorsProperties.getCode() + "400" + 1;
    }
}
