package io.github.jlmc.sbvalidation.api.errorhandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.lang.reflect.Field;
import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestControllerAdvice
public class BindExceptionAdvice {

    private final Logger logger = LoggerFactory.getLogger(BindExceptionAdvice.class);

    @Autowired
    Clock clock;
    @Autowired
    MessageSource messageSource;
    @Autowired
    ObjectMapper mapper;

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Problem> handler(WebExchangeBindException ex,
                                           ServerHttpRequest request) {

        Problem problem = Problem.createProblem(
                request.getPath().value(),
                400,
                "Bad request",
                "Request cannot be processed due to validation errors");

        List<Problem.Field> fields = toFieldErrors(ex.getBindingResult());
        problem.setFields(fields);
        return ResponseEntity.badRequest().body(problem);
    }

    private List<Problem.Field> toFieldErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors()
                            .stream()
                            .map(this::bindingResultViolationToApiErrorField)
                            .toList();
    }

    private Problem.Field bindingResultViolationToApiErrorField(ObjectError objectError) {
        logger.debug("Converting the field error [{}]", objectError);

        try {
            return mapObjectError(objectError);
        } catch (Exception e) {
            logger.warn(
                    "It was not possible to map the validation violation field, " +
                            "the default Strategy will be used to map the violation. " +
                            "the problem was caused by violation [{}]", objectError, e
            );
            return defaultViolationMapper(objectError);
        }
    }

    private Problem.Field mapObjectError(ObjectError objectError) {
        String fullInterpolatedJsonPathField = getInterpolatedFullJsonPath(objectError);
        return new Problem.Field(
                fullInterpolatedJsonPathField,
                errorMessage(objectError)
        );
    }

    private String getInterpolatedFullJsonPath(ObjectError objectError) {
        var constrainViolation =
                Optional.ofNullable(unwrapConstrainViolation(objectError))
                        .orElseThrow(() -> new IllegalArgumentException("No constrainViolation found in ObjectError: [{" + objectError + "}]"));


        var propertyPath = constrainViolation.getPropertyPath();
        var currentClass = constrainViolation.getRootBeanClass();

        var jsonPathBuilder = new StringBuilder();

        int i = 0;
        for (Path.Node node : propertyPath) {

            if (node.getName() == null || Objects.equals("", node.getName())) continue;

            if (node.getIndex() != null) {
                jsonPathBuilder.append("[").append(node.getIndex()).append("]");
            }

            if (Collection.class.isAssignableFrom(currentClass) && node instanceof NodeImpl nodeImpl) {
                currentClass = nodeImpl.getParent().getValue().getClass();
            }

            if (node.getKind() == ElementKind.PROPERTY) {
                var prefix = (i == 0) ? "$." : ".";
                var in = getInterpolatedJacksonName(currentClass, node.getName());
                jsonPathBuilder.append(prefix).append(in);
            }

            // define the next loop interaction been class
            currentClass = getFieldType(currentClass, node);

            i++;
        }

        return jsonPathBuilder.toString();
    }

    private String getInterpolatedJsonPropertyName(ObjectError objectError) {
        var constrainViolation =
                Optional.ofNullable(unwrapConstrainViolation(objectError))
                        .orElseThrow(() -> new IllegalArgumentException("No constrainViolation found in ObjectError: [{" + objectError + "}]"));

        var leafBean = constrainViolation.getLeafBean();
        Path.Node leafNode = getLeafNode(constrainViolation.getPropertyPath()).orElseThrow();

        if (leafBean != null && leafNode.getKind() == ElementKind.PARAMETER) {
            var name = leafNode.getName();
            return getInterpolatedJacksonName(leafBean.getClass(), name);
        } else {
            return null;
        }
    }

    private String getInterpolatedJacksonName(Class<?> beanClass, String nodeName) {
        try {
            var type = mapper.getTypeFactory().constructType(beanClass);
            var beanDescription = mapper.getSerializationConfig().introspect(type);
            var beanPropertyDefinitions = beanDescription.findProperties();
            return beanPropertyDefinitions
                    .stream()
                    .filter(beanPropertyDefinition -> beanPropertyDefinition.getField().getName().equals(nodeName))
                    .findFirst()
                    .map(BeanPropertyDefinition::getName)
                    .orElseThrow();

        } catch (Exception e) {
            logger.warn(
                    "Could not found the jackson property name for the node: [{}.{}], cause by [{}]",
                    beanClass.getSimpleName(),
                    nodeName,
                    e.getMessage()
            );

            throw e;
        }
    }

    private Class<?> getFieldType(Class<?> currentClass, Path.Node node) {
        return Optional.ofNullable(ReflectionUtils.findField(currentClass, node.getName()))
                       .map(Field::getType).
                       orElse(null);
    }

    private Problem.Field defaultViolationMapper(ObjectError objectError) {
        String name = (objectError instanceof FieldError f) ? f.getField() : objectError.getObjectName();
        return new Problem.Field(name, errorMessage(objectError));
    }

    private String errorMessage(ObjectError objectError) {
        return this.messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
    }

    private ConstraintViolationImpl<?> unwrapConstrainViolation(ObjectError objectError) {
        if (objectError.contains(ConstraintViolationImpl.class)) {
            return objectError.unwrap(ConstraintViolationImpl.class);
        } else {
            return null;
        }
    }

    private Optional<Path.Node> getLeafNode(Path path) {
        return StreamSupport.stream(path.spliterator(), false).reduce((a, b) -> b);
    }
}
