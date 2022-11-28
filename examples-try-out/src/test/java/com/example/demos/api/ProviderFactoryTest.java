package com.example.demos.api;

import com.example.demos.plugins.athena.AthenaBookProvider;
import com.example.demos.plugins.epic.EpicBookProvider;
import com.example.demos.providers.Book;
import com.example.demos.providers.BookProvider;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
//@Import({ProviderFactoryTest.XBookProvider.class, ProviderFactoryTest.YBookProvider.class})

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                ProviderFactory.class,
                AthenaBookProvider.class,
                EpicBookProvider.class,
                ProviderFactoryTest.XBookProvider.class,
                ProviderFactoryTest.YBookProvider.class,
        }
)
class ProviderFactoryTest {

    @Autowired
    ProviderFactory providerFactory;

    public static Stream<Arguments> beanQualifierMethodSource() {
        return Stream.of(
                Arguments.of("athena", AthenaBookProvider.class),
                Arguments.of("epic", EpicBookProvider.class)
        );
    }

    @ParameterizedTest(name = "[{index}] - when an valid qualified <{0}> value is passed it should resolve the bean")
    @MethodSource(value = "beanQualifierMethodSource")
    void when_an_valid_qualified_value_is_passed_it_should_resolve_the_bean(
            String qualifierValue,
            Class<?> expectedType
    ) {
        BookProvider bean = providerFactory.bookProvider(qualifierValue);

        System.out.println(bean);

        assertEquals(expectedType, bean.getClass());

        Class<? extends BookProvider> beanClass = bean.getClass();
        assertTrue(beanClass.isAnnotationPresent(Qualifier.class));
        Qualifier beanQualifier = beanClass.getAnnotation(Qualifier.class);
        assertNotNull(beanQualifier);
        assertEquals(qualifierValue, beanQualifier.value());

    }

    @ParameterizedTest(name = "[{index}] - when an invalid qualified value <{0}> is passed it should throw an exception")
    @ValueSource(strings = {"unknown", "no-unique"})
    void when_an_invalid_qualified_value_is_passed_it_should_throw_an_exception(String qualifierValue) {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> this.providerFactory.bookProvider(qualifierValue));

        System.out.println(exception);
    }

    @Component
    @Qualifier("no-unique")
    static class XBookProvider implements BookProvider {

        @Override
        public Flux<Book> search(String title) {
            return Flux.empty();
        }

        @Override
        public Mono<Book> getByIsbn(String isbn) {
            return Mono.empty();
        }

        @Override
        public String providerIdentifier() {
            return "no-unique";
        }
    }

    @Component
    @Qualifier("no-unique")
    static class YBookProvider implements BookProvider {

        @Override
        public Flux<Book> search(String title) {
            return Flux.empty();
        }

        @Override
        public Mono<Book> getByIsbn(String isbn) {
            return Mono.empty();
        }

        @Override
        public String providerIdentifier() {
            return "no-unique";
        }
    }
}
