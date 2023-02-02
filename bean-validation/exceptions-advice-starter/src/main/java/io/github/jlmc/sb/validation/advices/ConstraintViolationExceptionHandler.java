package io.github.jlmc.sb.validation.advices;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.github.jlmc.sb.validation.errors.ApiError;
import io.github.jlmc.sb.validation.errors.ErrorField;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class ConstraintViolationExceptionHandler implements ValidationAdvice<ConstraintViolationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintViolationExceptionHandler.class);

    private final ObjectMapper objectMapper;

    public ConstraintViolationExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private static List<Path.Node> getNodes(ConstraintViolation<?> constraintViolation) {
        Iterable<Path.Node> iterable = () -> constraintViolation.getPropertyPath().iterator();
        return StreamSupport.stream(iterable.spliterator(), false).toList();
    }

    private static Optional<ParameterDetails> parameterDetailsOf(RequestParameterType requestParameterType, Supplier<String> annotatedValueSupplier, String defaultName) {
        String name =
                Optional.ofNullable(annotatedValueSupplier.get())
                        .filter(it -> !it.isBlank())
                        .orElse(defaultName);

        return Optional.of(new ParameterDetails(requestParameterType, name));
    }

    @Override
    public Class<ConstraintViolationException> throwableType() {
        return ConstraintViolationException.class;
    }

    @Override
    public ApiError handler(Throwable throwable, String errorCode) {
        LOGGER.debug("Handling [{}] with the problem [{}]", throwable.getClass(), throwable.getMessage());

        return new ApiError(
                BAD_REQUEST.value(),
                errorCode,
                "Request cannot be processed due to validation errors",
                handleConstrainViolations(throwableType().cast(throwable).getConstraintViolations())
        );
    }

    private List<ErrorField> handleConstrainViolations(Set<ConstraintViolation<?>> constraintViolations) {
        return constraintViolations
                .stream()
                .map(this::handleConstrainViolation)
                .filter(Objects::nonNull)
                .toList()
                ;
    }

    private ErrorField handleConstrainViolation(ConstraintViolation<?> constraintViolation) {
        Path.Node leafNode = getLeafNode(constraintViolation.getPropertyPath()).orElse(null);

        if (leafNode == null) return null;

        return switch (leafNode.getKind()) {
            case PARAMETER -> handleInvalidParameter(constraintViolation);
            case PROPERTY -> handleInvalidProperty(constraintViolation);
            case BEAN -> handleInvalidBean(constraintViolation);
            default -> handleUnknownSource(constraintViolation);
        };
    }

    /**
     * Handle an invalid bean. Can be:
     * <pre>
     * 1. Invalid request bean (annotated bean class)
     * </pre>
     */
    private ErrorField handleInvalidBean(ConstraintViolation<?> constraintViolation) {
        return new ErrorField(
                RequestParameterType.ENTITY.title,
                constraintViolation.getMessage()
        );
    }

    /**
     * Handle an invalid property. Can be:
     * <pre>
     * 1. Invalid request parameter (annotated bean param field or annotated resource class field)
     * 2. Invalid request entity property (annotated bean param field)
     * </pre>
     */
    private ErrorField handleInvalidProperty(ConstraintViolation<?> constraintViolation) {
        Path.Node leafNode = getLeafNode(constraintViolation.getPropertyPath()).orElse(null);
        Class<?> beanClass = constraintViolation.getLeafBean().getClass();

        if (leafNode != null) {
            // Can be an invalid request parameter (annotated bean param field or annotated resource class field)
            Optional<Field> optionalField = getField(leafNode.getName(), beanClass);
            if (optionalField.isPresent()) {
                Field field = optionalField.get();
                List<Annotation> annotations = Arrays.stream(field.getAnnotations()).toList();
                Optional<ParameterDetails> optionalParameterDetails = getParameterDetails(annotations, field.getName());
                if (optionalParameterDetails.isPresent()) {
                    return createErrorForParameter(optionalParameterDetails.get(), constraintViolation);
                }
            }
        }

        if (leafNode != null) {
            // Can be an invalid request entity property (annotated bean param field)
            Optional<String> optionalJsonProperty = getJsonPropertyName(beanClass, leafNode.getName());

            if (optionalJsonProperty.isPresent()) {
                String name = optionalJsonProperty.get();
                return new ErrorField(
                        name,
                        "The %s '%s' %s".formatted(
                                RequestParameterType.ENTITY_PROPERTY.title,
                                name,
                                constraintViolation.getMessage())
                );
            }
        }

        return handleUnknownSource(constraintViolation);
    }

    private Optional<String> getJsonPropertyName(Class<?> beanClass, String nodeName) {
        JavaType javaType = objectMapper.getTypeFactory().constructType(beanClass);
        BeanDescription introspection = objectMapper.getSerializationConfig().introspect(javaType);
        List<BeanPropertyDefinition> properties = introspection.findProperties();

        return properties.stream()
                         .filter(propertyDefinition -> nodeName.equals(propertyDefinition.getField().getName()))
                         .map(BeanPropertyDefinition::getName)
                         .findFirst();
    }

    private ErrorField handleUnknownSource(ConstraintViolation<?> constraintViolation) {
        return new ErrorField(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
    }

    /**
     * Handle an invalid parameter. It Can be:
     * - Invalid request parameter (annotated method parameter)
     * - Invalid request entity (annotated method parameter)
     */
    private ErrorField handleInvalidParameter(ConstraintViolation<?> constraintViolation) {
        List<Path.Node> nodes = getNodes(constraintViolation);

        Path.Node parent = nodes.get(nodes.size() - 2);
        Path.Node child = nodes.get(nodes.size() - 1);

        if (ElementKind.METHOD != parent.getKind()) {
            return null;
        }

        Path.MethodNode methodNode = parent.as(Path.MethodNode.class);
        Path.ParameterNode parameterNode = child.as(Path.ParameterNode.class);
        Class<?> beanClass = constraintViolation.getLeafBean().getClass();

        List<Annotation> annotations = getParameterAnnotations(beanClass, methodNode, parameterNode);

        Optional<ParameterDetails> parameterDetails = getParameterDetails(annotations, child.getName());

        return parameterDetails.map(it ->
                new ErrorField(
                        it.name(),
                        "The %s '%s' %s".formatted(it.type().title, it.name, constraintViolation.getMessage())

                )
        ).orElse(null);
    }

    private List<Annotation> getParameterAnnotations(Class<?> beanClass, Path.MethodNode methodNode, Path.ParameterNode parameterNode) {
        List<Annotation> annotations = new ArrayList<>();

        try {
            var parameterTypes = methodNode.getParameterTypes().toArray(Class[]::new);
            Method method = beanClass.getMethod(methodNode.getName(), parameterTypes);

            annotations.addAll(Arrays.asList(method.getParameterAnnotations()[parameterNode.getParameterIndex()]));

            Method interfaceMethod = ClassUtils.getInterfaceMethodIfPossible(method, beanClass);

            if (method != interfaceMethod) {
                annotations.addAll(Arrays.asList(interfaceMethod.getParameterAnnotations()[parameterNode.getParameterIndex()]));
            }
        } catch (NoSuchMethodException e) {
            LOGGER.warn("Could not get Parameter Annotations {}#{}({})", beanClass, methodNode, parameterNode, e);
        }

        return annotations;
    }

    private Optional<Path.Node> getLeafNode(Path path) {
        return StreamSupport.stream(path.spliterator(), false).reduce((a, b) -> b);
    }

    Optional<ParameterDetails> getParameterDetails(List<Annotation> annotations, String defaultName) {
        for (Annotation annotation : annotations) {
            Optional<ParameterDetails> optionalParameterDetails = getParameterDetails(annotation, defaultName);
            if (optionalParameterDetails.isPresent()) {
                return optionalParameterDetails;
            }
        }

        return Optional.empty();
    }

    private Optional<ParameterDetails> getParameterDetails(Annotation annotation, String defaultName) {
        if (annotation instanceof RequestParam requestParam) {
            return parameterDetailsOf(RequestParameterType.QUERY, requestParam::value, defaultName);

        } else if (annotation instanceof PathVariable pathVariable) {
            return parameterDetailsOf(RequestParameterType.PATH, pathVariable::value, defaultName);

        } else if (annotation instanceof RequestHeader requestHeader) {
            return parameterDetailsOf(RequestParameterType.HEADER, requestHeader::value, defaultName);

        } else if (annotation instanceof CookieValue cookieValue) {
            return parameterDetailsOf(RequestParameterType.COOKIE, cookieValue::value, defaultName);

        } else if (annotation instanceof MatrixVariable matrixVariable) {
            return parameterDetailsOf(RequestParameterType.MATRIX, matrixVariable::value, defaultName);
        }

        return Optional.empty();
    }

    private Optional<Field> getField(String fieldName, Class<?> beanClass) {
        return Arrays.stream(beanClass.getDeclaredFields())
                     .filter(field -> field.getName().equals(fieldName))
                     .findFirst();
    }

    private ErrorField createErrorForParameter(ParameterDetails parameterDetails, ConstraintViolation<?> constraintViolation) {
        return new ErrorField(
                parameterDetails.name(),
                "The %s '%s' %s".formatted(parameterDetails.type().title, parameterDetails.name, constraintViolation.getMessage())
        );
    }

    private enum RequestParameterType {
        QUERY("Query parameter"),
        PATH("Path parameter"),
        HEADER("Header parameter"),
        COOKIE("Cookie parameter"),
        MATRIX("Matrix parameter"),
        ENTITY_PROPERTY("Entity property"),
        ENTITY("Entity");

        private final String title;

        RequestParameterType(String title) {
            this.title = title;
        }
    }

    //@formatter:on
    private record ParameterDetails(RequestParameterType type, String name) {
    }
    //@formatter:off
}
