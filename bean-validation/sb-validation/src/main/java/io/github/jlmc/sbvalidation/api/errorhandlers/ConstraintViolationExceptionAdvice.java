package io.github.jlmc.sbvalidation.api.errorhandlers;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static io.github.jlmc.sbvalidation.api.errorhandlers.ConstraintViolationExceptionAdvice.RequestFieldType.REQUEST_ENTITY_PROPERTY;

/**
 * @see org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler
 */
@RestControllerAdvice
public class ConstraintViolationExceptionAdvice {

    final Clock clock;

    final ObjectMapper objectMapper;

    public ConstraintViolationExceptionAdvice(Clock clock, ObjectMapper objectMapper) {
        this.clock = clock;
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Problem> handler(ConstraintViolationException ex,
                                           ServerHttpRequest request) {

        Problem problem = Problem.createProblem(
                request.getPath().value(),
                400,
                "Bad request",
                "Request cannot be processed due to validation errors");

        List<Problem.Field> fields = toFieldErrors(ex.getConstraintViolations());
        problem.setFields(fields);
        return ResponseEntity.badRequest().body(problem);
    }

    /**
     * Map a {@link ConstraintViolation}S to an {@link Problem.Field}.
     */
    List<Problem.Field> toFieldErrors(Collection<ConstraintViolation<?>> constraintViolations) {

        return constraintViolations.stream()
                .map(constraintViolation -> {
                    Path.Node leafNode = getLeafNode(constraintViolation.getPropertyPath()).get();

                    if (ElementKind.PARAMETER == leafNode.getKind()) {
                        return handleInvalidParameter(constraintViolation);
                    }

                    if (ElementKind.PROPERTY == leafNode.getKind()) {
                        return handleInvalidProperty(constraintViolation);
                    }

                    if (ElementKind.BEAN == leafNode.getKind()) {
                        return handleInvalidBean(constraintViolation);
                    } else {
                        return handleUnknownSource(constraintViolation);
                    }
                }).toList();
    }

    /**
     * Handle an invalid parameter. Can be:
     * <p>
     * 1. Invalid request parameter (annotated method parameter)
     * 2. Invalid request entity (annotated method parameter)
     */
    private Problem.Field handleInvalidParameter(ConstraintViolation<?> constraintViolation) {
        List<Path.Node> nodes = new ArrayList<>();
        constraintViolation.getPropertyPath().iterator().forEachRemaining(nodes::add);

        Path.Node parent = nodes.get(nodes.size() - 2);
        Path.Node child = nodes.getLast();

        if (ElementKind.METHOD == parent.getKind()) {

            Path.MethodNode methodNode = parent.as(Path.MethodNode.class);
            Path.ParameterNode parameterNode = child.as(Path.ParameterNode.class);

            try {
                // Can be an invalid request parameter (annotated method parameter)
                Class<?> beanClass = constraintViolation.getLeafBean().getClass();
                Method method = beanClass.getMethod(methodNode.getName(), methodNode.getParameterTypes().toArray(Class[]::new));

                List<Annotation> parameterAnnotations = getParameterAnnotations(method, beanClass, parameterNode.getParameterIndex());

                Optional<ParameterDetails> parameterDetails = getParameterDetails(parameterAnnotations);


                return parameterDetails
                        .map(it -> new Problem.Field(it.name, "%s %s %s".formatted(it.type.name(), it.name, constraintViolation.getMessage())))
                        .orElseGet(() -> new Problem.Field(child.getName(), constraintViolation.getMessage()));

            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        throw new IllegalArgumentException();
    }

    private Problem.Field problemFieldOf(ParameterDetails parameterDetails, ConstraintViolation<?> constraintViolation) {
        return new Problem.Field(parameterDetails.name, "%s %s %s".formatted(parameterDetails.type.name(), parameterDetails.name, constraintViolation.getMessage()));
    }

    private Problem.Field handleUnknownSource(ConstraintViolation<?> constraintViolation) {
        return new Problem.Field(
                constraintViolation.getPropertyPath().toString(),
                constraintViolation.getMessage()
        );
    }

    private Optional<ParameterDetails> getParameterDetails(Collection<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            Optional<ParameterDetails> parameterDetails = getParameterDetails(annotation);
            if (parameterDetails.isPresent())
                return parameterDetails;
        }
        return Optional.empty();
    }

    private Optional<ParameterDetails> getParameterDetails(Annotation annotation) {
        return switch (annotation) {
            case RequestParam a -> Optional.of(new ParameterDetails(RequestFieldType.QUERY_PARAMETER, a.value()));
            case PathVariable a -> Optional.of(new ParameterDetails(RequestFieldType.PATH_PARAMETER, a.value()));
            case RequestHeader a -> Optional.of(new ParameterDetails(RequestFieldType.HEADER_PARAMETER, a.value()));
            case CookieValue a -> Optional.of(new ParameterDetails(RequestFieldType.COOKIE_PARAMETER, a.value()));
            case MatrixVariable a -> Optional.of(new ParameterDetails(RequestFieldType.MATRIX_PARAMETER, a.value()));
            case null, default -> Optional.empty();
        };
    }

    /**
     * Get the leaf node.
     */
    private Optional<Path.Node> getLeafNode(Path path) {
        return StreamSupport.stream(path.spliterator(), false).reduce((a, b) -> b);
    }

    private List<Annotation> getParameterAnnotations(
            Method method,
            Class<?> beanClass,
            int parameterIndex) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Annotation[] parameterAnnotation = parameterAnnotations[parameterIndex];
        List<Annotation> allAnnotations = new ArrayList<>(Arrays.stream(parameterAnnotation).toList());

        Method interfaceMethod = ClassUtils.getInterfaceMethodIfPossible(method, beanClass);
        if (method != interfaceMethod) {
            List<Annotation> annotations = Arrays.stream(interfaceMethod.getParameterAnnotations()[parameterIndex]).toList();
            allAnnotations.addAll(annotations);
        }
        return allAnnotations;
    }

    //// -----

    /**
     * Handle an invalid property. Can be:
     * - Invalid request parameter (annotated bean param field or annotated resource class field)
     * - Invalid request entity property (annotated bean param field)
     */
    private Problem.Field handleInvalidProperty(ConstraintViolation<?> constraintViolation) {

        Path.Node leafNode = getLeafNode(constraintViolation.getPropertyPath()).get();
        Class<?> beanClass = constraintViolation.getLeafBean().getClass();

        // Can be an invalid request parameter (annotated bean param field or annotated resource class field)
        Field field = getField(leafNode.getName(), beanClass);
        if (field != null) {
            Optional<ParameterDetails> parameterDetails = getParameterDetails(Arrays.stream(field.getAnnotations()).toList());
            if (parameterDetails.isPresent()) {
                return problemFieldOf(parameterDetails.get(), constraintViolation);
            }
        }

        // Can be an invalid request entity property (annotated bean param field)
        String jsonProperty = getJsonPropertyName(objectMapper, beanClass, leafNode.getName());

        if (jsonProperty != null) {
            return new Problem.Field(jsonProperty,
                    "%s %s %s".formatted(
                            REQUEST_ENTITY_PROPERTY.name(),
                            jsonProperty,
                            constraintViolation.getMessage()));
        }

        return handleUnknownSource(constraintViolation);
    }

    /**
     * Handle an invalid bean. Can be:
     * <p>
     * 1. Invalid request bean (annotated bean class)
     */
    private Problem.Field handleInvalidBean(ConstraintViolation<?> constraintViolation) {
        return new Problem.Field(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
    }

    private String getJsonPropertyName(ObjectMapper mapper, Class<?> beanClass, String nodeName) {

        JavaType javaType = getJavaType(mapper, beanClass);
        BeanDescription introspection = mapper.getSerializationConfig().introspect(javaType);
        List<BeanPropertyDefinition> properties = introspection.findProperties();

        return properties.stream()
                .filter(propertyDefinition -> nodeName.equals(propertyDefinition.getField().getName()))
                .map(BeanPropertyDefinition::getName)
                .findFirst()
                .orElse(null);
    }

    private static JavaType getJavaType(ObjectMapper mapper, Class<?> beanClass) {
        return mapper.getTypeFactory().constructType(beanClass);
    }

    private Field getField(String fieldName, Class<?> beanClass) {
        return Arrays.stream(beanClass.getDeclaredFields())
                .filter(field -> field.getName().equals(fieldName))
                .findFirst()
                .orElse(null);
    }


    enum RequestFieldType {
        QUERY_PARAMETER,
        PATH_PARAMETER,
        HEADER_PARAMETER,
        COOKIE_PARAMETER,
        MATRIX_PARAMETER,

        REQUEST_ENTITY_PROPERTY,

        REQUEST_ENTITY
    }

    record ParameterDetails(RequestFieldType type, String name) {
    }
}
