package com.example.demos.api;

import com.example.demos.providers.BookProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.stereotype.Component;

@Component
public class ProviderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderFactory.class);

    private final BeanFactory beanFactory;

    public ProviderFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BookProvider bookProvider(String providerIdentifier) {
        try {
            return BeanFactoryAnnotationUtils
                    .qualifiedBeanOfType(
                            beanFactory,
                            BookProvider.class,
                            providerIdentifier);
        } catch (BeansException e) {
            // it can throw an NoUniqueBeanDefinitionException, NoSuchBeanDefinitionException or a more generic BeansException
            LOGGER.error(e.getMessage(), e);
            throw new IllegalArgumentException(
                    "The %s bean with the Qualifier '%s' can't be resolver, %s".formatted(
                            BookProvider.class.getSimpleName(),
                            providerIdentifier,
                            e.getMessage()));
        }
    }
}
