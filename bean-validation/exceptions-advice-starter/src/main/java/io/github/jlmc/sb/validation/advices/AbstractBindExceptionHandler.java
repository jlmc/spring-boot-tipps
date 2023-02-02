package io.github.jlmc.sb.validation.advices;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.jlmc.sb.validation.errors.ApiError;
import io.github.jlmc.sb.validation.errors.ErrorField;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

abstract class AbstractBindExceptionHandler implements ExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBindExceptionHandler.class);

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    public AbstractBindExceptionHandler(ObjectMapper objectMapper, MessageSource messageSource) {
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }


    @Override
    public ApiError handler(Throwable throwable, String errorCode) {
        LOGGER.debug("Handling [{}] with the problem [{}]", throwable.getClass(), throwable.getMessage());

        var errorFields =
                getBindingResult(throwable)
                        .getAllErrors()
                        .stream()
                        .map(this::bindingResultViolationToApiErrorField)
                        .sorted(Comparator.comparing(ErrorField::name))
                        .toList();

        return new ApiError(
                BAD_REQUEST.value(),
                errorCode,
                "Request cannot be processed due to validation errors",
                errorFields
        );
    }

    abstract BindingResult getBindingResult(Throwable throwable);

    protected ErrorField bindingResultViolationToApiErrorField(ObjectError objectError) {
        LOGGER.debug("Converting the field error [{}]", objectError);

        try {
            return new ErrorField(
                    buildInterpolatedFullPathJsonPath(objectError),
                    errorMessage(objectError)
            );
        } catch (Exception e) {
            LOGGER.warn(
                    "It was not possible to map the validation violation field, " +
                            "the default Strategy will be used to map the violation. " +
                            "The problem was caused by the violation [{}]", objectError, e
            );
            return defaultViolationMapper(objectError);
        }
    }

    private String buildInterpolatedFullPathJsonPath(ObjectError objectError) {
        JsonPathAccumulator jsonPathAccumulator = new JsonPathAccumulator();

        ConstraintViolation<?> constrainViolation = unwrapConstrainViolation(objectError);
        Class<?> currentBeanClass = constrainViolation.getRootBeanClass();

        for (Path.Node pathNode : constrainViolation.getPropertyPath()) {
            Integer index = pathNode.getIndex();

            if (index != null) {
                jsonPathAccumulator.index(index);
            }

            if (pathNode.getKind() == ElementKind.PROPERTY && pathNode.isInIterable()) {
                if (pathNode instanceof NodeImpl node) {
                    Optional<NodeImpl> parent = Optional.ofNullable(node.getParent());
                    parent.map(NodeImpl::getValue)
                          .map(Object::getClass)
                          .flatMap(clazz -> getJacksonPropertyDefinition(clazz, pathNode.getName()))
                          .map(BeanPropertyDefinition::getName)
                          .ifPresent(jsonPathAccumulator::append);

                }
            } else if (pathNode.getKind() == ElementKind.PROPERTY) {
                BeanPropertyDefinition propertyDefinition =
                        getJacksonPropertyDefinition(currentBeanClass, pathNode.getName())
                                .orElseThrow();

                jsonPathAccumulator.append(propertyDefinition.getName());

                // define the next loop interaction been class
                currentBeanClass = propertyDefinition.getRawPrimaryType();
            }
        }

        return jsonPathAccumulator.toString();
    }

    private Optional<BeanPropertyDefinition> getJacksonPropertyDefinition(Class<?> beanClass, String nodeName) {
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        JavaType javaType = typeFactory.constructType(beanClass);
        BeanDescription beanDescription = objectMapper.getSerializationConfig().introspect(javaType);

        List<BeanPropertyDefinition> properties = beanDescription.findProperties();

        return properties.stream()
                         .filter(it -> Objects.equals(nodeName, it.getField().getName()))
                         .findFirst();
    }

    private ConstraintViolationImpl<?> unwrapConstrainViolation(ObjectError objectError) {
        if (!objectError.contains(ConstraintViolationImpl.class)) {
            throw new IllegalArgumentException("The object error " + objectError + " do not contains " + ConstraintViolationImpl.class.getName());
        }

        return objectError.unwrap(ConstraintViolationImpl.class);
    }

    private ErrorField defaultViolationMapper(ObjectError objectError) {
        String name = objectError.getObjectName();

        if (objectError instanceof FieldError fieldError) {
            name = fieldError.getField();
        }

        return new ErrorField(
                name,
                errorMessage(objectError)
        );
    }

    private String errorMessage(ObjectError objectError) {
        return messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
    }

    private static class JsonPathAccumulator {
        private final StringBuilder sb = new StringBuilder();

        void append(String part) {
            if (!sb.isEmpty()) {
                sb.append(".");
            }
            sb.append(part);
        }

        void index(int index) {
            sb.append("[").append(index).append("]");
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }
}
