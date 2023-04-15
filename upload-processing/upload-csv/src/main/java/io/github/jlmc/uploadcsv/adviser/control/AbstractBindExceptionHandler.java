package io.github.jlmc.uploadcsv.adviser.control;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.github.jlmc.uploadcsv.adviser.entity.ApiError;
import io.github.jlmc.uploadcsv.adviser.entity.ErrorField;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

abstract class AbstractBindExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBindExceptionHandler.class);

    private final MessageSource messageSource;
    private final ObjectMapper mapper;

    public AbstractBindExceptionHandler(MessageSource messageSource, ObjectMapper mapper) {
        this.messageSource = messageSource;
        this.mapper = mapper;
    }

    public ApiError handler(BindingResult bindingResult, String errorCode) {
        return new ApiError(
                errorCode,
                "Request cannot be processed due to validation errors",
                bindingResult.getAllErrors()
                             .stream()
                             .map(this::bindingResultViolationToApiErrorField)
                             .flatMap(Optional::stream)
                             .sorted(Comparator.comparing(ErrorField::name))
                             .toList()
        );
    }

    private Optional<ErrorField> bindingResultViolationToApiErrorField(ObjectError objectError) {
        LOGGER.debug("Converting the field error [{}]", objectError);

        try {
            return Optional.of(new ErrorField(buildInterpolatedFullPathJsonPath(objectError), errorMessage(objectError)));
        } catch (Exception e) {
            LOGGER.warn(
                    "It was not possible to map the validation violation field, " +
                    "the default Strategy will be used to map the violation. " +
                    "The problem was caused by the violation [{}]", objectError, e
            );

            return Optional.ofNullable(objectError).map(this::defaultViolationMapper);
        }
    }

    private String errorMessage(ObjectError objectError) {
        return messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
    }

    private ErrorField defaultViolationMapper(ObjectError objectError) {
        String name;
        if (objectError instanceof FieldError fe) {
            name = fe.getField();
        } else {
            name = objectError.getObjectName();
        }
        return new ErrorField(name, errorMessage(objectError));
    }

    private String buildInterpolatedFullPathJsonPath(ObjectError objectError) {
        JsonPathAccumulator jsonPathBuilder = new JsonPathAccumulator();

        ConstraintViolation<?> constraintViolation = unwrapConstrainViolation(objectError);
        Class<?> currentBeanClass = constraintViolation.getRootBeanClass();

        for (Path.Node pathNode : constraintViolation.getPropertyPath()) {

            // when every the node contain index append the index value,
            // inclusive if is the first element
            Integer index = pathNode.getIndex();
            if (index != null) {
                jsonPathBuilder.index(index);
            }

            if (ElementKind.PROPERTY == pathNode.getKind() && pathNode.isInIterable()) {
                if (pathNode instanceof NodeImpl node) {
                    Optional.ofNullable(node.getParent())
                            .map(NodeImpl::getValue)
                            .map(Object::getClass)
                            .map(clazz -> getJacksonPropertyDefinition(clazz, pathNode.getName()))
                            .map(BeanPropertyDefinition::getName)
                            .ifPresent(jsonPathBuilder::append);
                }
            } else if (ElementKind.PROPERTY == pathNode.getKind()) {
                var propertyDefinition = getJacksonPropertyDefinition(currentBeanClass, pathNode.getName());
                jsonPathBuilder.append(propertyDefinition.getName());

                // define the next loop interaction been class
                currentBeanClass = propertyDefinition.getRawPrimaryType();
            }
        }

        return jsonPathBuilder.asString();
    }

    //     private fun getJacksonPropertyDefinition(beanClass: Class<*>, nodeName: String): BeanPropertyDefinition {
    private BeanPropertyDefinition getJacksonPropertyDefinition(Class<?> beanClass, String nodeName) {
        JavaType type = mapper.getTypeFactory().constructType(beanClass);
        BeanDescription beanDescription = mapper.getSerializationConfig().introspect(type);
        List<BeanPropertyDefinition> beanPropertyDefinitions = beanDescription.findProperties();

        return beanPropertyDefinitions.stream()
                                      .filter(beanPropertyDefinition ->
                                              Objects.equals(nodeName, beanPropertyDefinition.getField().getName()))
                                      .findFirst()
                                      .orElseThrow();
    }


    private ConstraintViolation<?> unwrapConstrainViolation(ObjectError o) {
        Assert.isTrue(o.contains(ConstraintViolationImpl.class),
                () -> "the Object Error does not contains " + ConstraintViolation.class.getName());
        return o.unwrap(ConstraintViolation.class);
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

        String asString() {
            return sb.toString();
        }

        @Override
        public String toString() {
            return asString();
        }
    }
}
