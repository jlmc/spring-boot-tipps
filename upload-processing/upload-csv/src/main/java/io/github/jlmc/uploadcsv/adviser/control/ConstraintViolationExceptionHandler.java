package io.github.jlmc.uploadcsv.adviser.control;

import io.github.jlmc.uploadcsv.adviser.entity.ApiError;
import io.github.jlmc.uploadcsv.adviser.entity.ErrorField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class ConstraintViolationExceptionHandler implements ExceptionHandler<ConstraintViolationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintViolationExceptionHandler.class);

    private static Method getMethod(Class<?> beanClass, Path.MethodNode methodNode) {
        try {
            return beanClass.getMethod(
                    methodNode.getName(),
                    methodNode.getParameterTypes().toArray(new Class[0]));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static ErrorField createDefaultErrorField(String name, String description) {
        return new ErrorField(name, description);
    }

    private static LinkedList<Path.Node> nodeList(ConstraintViolation<?> constraintViolation) {
        return StreamSupport.stream(constraintViolation.getPropertyPath().spliterator(), false).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public ApiError handler(ConstraintViolationException exception, String errorCode) {
        LOGGER.debug("Handling [{}] with the problem [{}]", exception.getClass(), exception.getMessage());
        return new ApiError(
                errorCode,
                "Request cannot be processed due to validation errors",
                handleConstrainViolations(exception)
        );
    }

    private List<ErrorField> handleConstrainViolations(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                        .map(this::handleConstrainViolation)
                        .filter(Objects::nonNull)
                        .toList();
    }

    private ErrorField handleConstrainViolation(ConstraintViolation<?> constraintViolation) {
        var nodes = nodeList(constraintViolation);
        if (!nodes.isEmpty()) {
            Path.Node last = nodes.getLast();
            if (ElementKind.PARAMETER == last.getKind()) {
                return handleInvalidParameter(constraintViolation);
            }
        }

        return createDefaultErrorField(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
    }

    /**
     * Handle an invalid parameter. It Can be:
     * - Invalid request parameter (annotated method parameter)
     * - Invalid request entity (annotated method parameter)
     */
    private ErrorField handleInvalidParameter(ConstraintViolation<?> constraintViolation) {
        var nodes = nodeList(constraintViolation);

        var parent = nodes.get(nodes.size() - 2);

        if (ElementKind.METHOD != parent.getKind()) {
            return null;
        }

        var child = nodes.get(nodes.size() - 1);
        var methodNode = parent.as(Path.MethodNode.class);
        var parameterNode = child.as(Path.ParameterNode.class);
        Class<?> beanClass = constraintViolation.getLeafBean().getClass();

        var parameterAnnotations = getParameterAnnotations(beanClass, methodNode, parameterNode);

        Optional<ParameterDetails> parameterDetails = getParameterDetails(parameterAnnotations);

        return parameterDetails
                .map(it -> new ErrorField(
                        it.name,
                        "The %s '%s' %s".formatted(it.type.title, it.name, constraintViolation.getMessage())))
                .orElseGet(() -> createDefaultErrorField(child.getName(), constraintViolation.getMessage()));
    }

    private List<Annotation> getParameterAnnotations(Class<?> beanClass, Path.MethodNode methodNode, Path.ParameterNode parameterNode) {
        Method method = getMethod(beanClass, methodNode);

        List<Annotation> parameterAnnotations =
                new ArrayList<>(Arrays.asList(method.getParameterAnnotations()[parameterNode.getParameterIndex()]));

        Method interfaceMethod = ClassUtils.getInterfaceMethodIfPossible(method, beanClass);
        //noinspection ConstantValue
        if (interfaceMethod != null && interfaceMethod != method) {
            parameterAnnotations.addAll(Arrays.asList(interfaceMethod.getParameterAnnotations()[parameterNode.getParameterIndex()]));
        }

        return parameterAnnotations;
    }

    private Optional<ParameterDetails> getParameterDetails(List<Annotation> annotations) {

        ParameterDetails parameterDetails = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof RequestParam rp) {
                parameterDetails = new ParameterDetails(RequestParameterType.QUERY, nameOr(rp.name(), rp.value()));
                break;
            }
            if (annotation instanceof PathVariable rp) {
                parameterDetails = new ParameterDetails(RequestParameterType.PATH, nameOr(rp.name(), rp.value()));
                break;
            }
            if (annotation instanceof RequestHeader rp) {
                parameterDetails = new ParameterDetails(RequestParameterType.HEADER, nameOr(rp.name(), rp.value()));
                break;
            }
            if (annotation instanceof CookieValue rp) {
                parameterDetails = new ParameterDetails(RequestParameterType.COOKIE, nameOr(rp.name(), rp.value()));
                break;
            }
            if (annotation instanceof MatrixVariable rp) {
                parameterDetails = new ParameterDetails(RequestParameterType.MATRIX, nameOr(rp.name(), rp.value()));
                break;
            }
        }

        return Optional.ofNullable(parameterDetails);
    }

    private String nameOr(String name, String alternative) {
        if (name == null || name.isBlank()) {
            return alternative;
        }
        return name;
    }

    enum RequestParameterType {
        QUERY("Query parameter"),
        PATH("Path parameter"),
        HEADER("Header parameter"),
        COOKIE("Cookie parameter"),
        MATRIX("Matrix parameter");

        private final String title;

        RequestParameterType(String title) {
            this.title = title;
        }
    }

    record ParameterDetails(RequestParameterType type, String name) {
    }

}
