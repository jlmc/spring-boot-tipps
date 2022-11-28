# 9.14. Use Spring Resource Bundle as Bean Validation Resource Bundle

## Summary:
- configure project spring-boot so that Spring-Resource-Bundle is used as Bean Validation


## Execution 

```java
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidationConfig {

    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
}
```


2. Now if we have some `ValidationMessage.properties` file in our project we can removed him and redefine the templates in `src/main/resources/messages.properties` file as following:

```properties
# This is the file: src/main/resources/messages.properties

restaurant.name=The restaurant name

# if we do no define the override template then the default defined in ValidationMessage.properties It will be used
NotNull={0} is mandatory
NotBlank={0} must not be blank
Size={0} should have a length between {min} and {max}
```
