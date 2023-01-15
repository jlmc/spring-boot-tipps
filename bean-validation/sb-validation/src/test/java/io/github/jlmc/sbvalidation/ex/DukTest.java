package io.github.jlmc.sbvalidation.ex;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.github.jlmc.sbvalidation.movies.boundary.MovieFilter;
import io.github.jlmc.sbvalidation.movies.entity.Details;
import io.github.jlmc.sbvalidation.movies.entity.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DukTest {


    @Autowired
    ObjectMapper mapper;

    @Test
    void name() {
        Class<?> beanClass = Details.class;

        String nodeName = "mainActor";

        JavaType javaType = mapper.getTypeFactory().constructType(beanClass);
        BeanDescription introspection = mapper.getSerializationConfig().introspect(javaType);
        List<BeanPropertyDefinition> properties = introspection.findProperties();

        var sas =  properties.stream()
                         .filter(propertyDefinition -> nodeName.equals(propertyDefinition.getField().getName()))
                         .map(BeanPropertyDefinition::getName)
                         .findFirst()
                         .orElse(null);

        System.out.println(sas);
    }
}
