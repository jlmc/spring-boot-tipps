package io.github.jlmc.sb.validation.advices;

public interface ValidationAdvice<E extends Throwable> extends ExceptionHandler {

    Class<E> throwableType();
}
