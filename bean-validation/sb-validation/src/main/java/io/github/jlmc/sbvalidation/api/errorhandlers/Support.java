package io.github.jlmc.sbvalidation.api.errorhandlers;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;

import java.util.List;

public class Support {

    static String getJsonPropertyName(
            ObjectMapper mapper,
            Class<?> beanClass,
            String nodeName) {
        JavaType javaType = mapper.getTypeFactory().constructType(beanClass);
        BeanDescription introspection = mapper.getSerializationConfig().introspect(javaType);
        List<BeanPropertyDefinition> properties = introspection.findProperties();

        return properties.stream()
                         .filter(propertyDefinition -> nodeName.equals(propertyDefinition.getField().getName()))
                         .map(BeanPropertyDefinition::getName)
                         .findFirst()
                         .orElse(null);
    }


    static String getPropertyName(ObjectMapper mapper, ConstraintViolation<?> violation) {
        Path propertyPath = violation.getPropertyPath();
        String propertyName = getPropertyName(mapper,violation.getRootBeanClass(), propertyPath);
        //return new Violation(this.formatFieldName(propertyName), violation.getMessage());




        return propertyName;
    }


    static String getPropertyName(ObjectMapper mapper, Class<?> clazz, Path propertyPath) {
        String defaultName = propertyPath.toString();

        JavaType type = mapper.constructType(clazz);
        BeanDescription desc = mapper.getSerializationConfig().introspect(type);
        return desc.findProperties()
                   .stream()
                   .filter(prop -> prop.getInternalName().equals(defaultName))
                   .map(BeanPropertyDefinition::getName)
                   .findFirst()
                   .orElse(defaultName);
    }


    static String getJsonPropertyName2(ObjectMapper mapper, Class<?> beanClass, String nodeName) {

        JavaType javaType = mapper.getTypeFactory().constructType(beanClass);
        BeanDescription introspection = mapper.getSerializationConfig().introspect(javaType);
        List<BeanPropertyDefinition> properties = introspection.findProperties();

        return properties.stream()
                         .filter(propertyDefinition -> nodeName.equals(propertyDefinition.getField().getName()))
                         .map(BeanPropertyDefinition::getName)
                         .findFirst()
                         .orElse(null);
    }
}
