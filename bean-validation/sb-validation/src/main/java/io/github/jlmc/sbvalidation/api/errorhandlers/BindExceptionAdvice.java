package io.github.jlmc.sbvalidation.api.errorhandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class BindExceptionAdvice {

    private static final String CAMEL_REGEX = "(?<=[a-zA-Z])[A-Z]";
    @Autowired
    Clock clock;
    @Autowired
    MessageSource messageSource;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ConstraintViolationExceptionAdvice constraintViolationExceptionAdvice;

    private static ConstraintViolation<?> extractConstraintViolation(ObjectError objectError) {
        try {
            Field violation = objectError.getClass().getDeclaredField("violation");
            if (violation != null) {

                violation.setAccessible(true);
                return (ConstraintViolation<?>) violation.get(objectError);
            } else {
                return null;
            }
        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static String camelToSnakeCase(String txt) {
        String replacement = "$1_$2";
        return txt.replaceAll(CAMEL_REGEX, replacement).toLowerCase();
    }

    @ExceptionHandler({WebExchangeBindException.class}) // WebExchangeBindException
    public ResponseEntity<Problem> webExchangeBindExceptionHandler(
            WebExchangeBindException th,
            ServerHttpRequest request) {

        return bindingExceptionHandler(th.getBindingResult(), request);
    }

    private ResponseEntity<Problem> bindingExceptionHandler(BindingResult bindingResult, ServerHttpRequest request) {
        List<Problem.Field> fields = toErrorFields(bindingResult);
        Problem problem = Problem.createProblem(
                                         Instant.now(clock),
                                         request.getPath().value(),
                                         400,
                                         "Bad request",
                                         "Request cannot be processed due to validation errors")
                                 .setFields(fields);
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Problem> methodArgumentNotValidException(MethodArgumentNotValidException e, ServerHttpRequest request) {
        return bindingExceptionHandler(e.getBindingResult(), request);
    }

    private List<Problem.Field> toErrorFields(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                            .map((ObjectError objectError) -> {


                                String name = "";
                                if (objectError instanceof FieldError fieldError) {
                                    name = fieldError.getField();
                                    Object rejectedValue = fieldError.getRejectedValue();
                                } else {
                                    name = objectError.getObjectName();

                                }

                                problemField(objectError);


                                String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());

                                return new Problem.Field(name, message);

                            })
                            .toList();
    }

    public Problem.Field problemField(ObjectError objectError) {
        ConstraintViolation<?> constraintViolation = extractConstraintViolation(objectError);
        //  List<Problem.Field> fields =
        //  constraintViolationExceptionAdvice.toFieldErrors(List.of(constraintViolation));

        Path propertyPath = constraintViolation.getPropertyPath();
        Class<?> rootBeanClass = constraintViolation.getRootBeanClass();
        Object leafBean = constraintViolation.getLeafBean();


        Class clazz = rootBeanClass;
        StringBuilder cb = new StringBuilder();
        int interactions = 0;
        for (Path.Node node : propertyPath) {



            ElementKind kind = node.getKind();
            Integer index = node.getIndex();
            String name = node.getName();

            System.out.println(kind);
            if (node.getName().equals("")) continue;

            String jsonPropertyName2 = Support.getJsonPropertyName2(mapper, clazz, name);

            System.out.println("---> " + jsonPropertyName2);
            if (kind == ElementKind.PROPERTY) {
                String prefix = interactions == 0 ? "$." : ".";
                cb.append(prefix);
                cb.append(jsonPropertyName2);
            }

            if (index != null) {
                cb.append("[" + index + "]");
            }

            interactions++;


            if (interactions > 0 && clazz != null) {
                clazz = getaClass(clazz, name);
            }
        }


        String propertyName = Support.getPropertyName(mapper, constraintViolation);


        return null;
    }

    private static Class getaClass(Class parent, String fieldName) {
        try {
            Field declaredField = parent.getDeclaredField(fieldName);
            return declaredField.getType();
        } catch (NoSuchFieldException e) {
            return null;
        }
    }


}
