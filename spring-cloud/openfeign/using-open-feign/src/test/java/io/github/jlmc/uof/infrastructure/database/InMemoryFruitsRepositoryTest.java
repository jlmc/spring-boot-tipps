package io.github.jlmc.uof.infrastructure.database;

import io.github.jlmc.uof.domain.fruits.entities.Fruit;
import io.github.jlmc.uof.domain.fruits.ports.outgoing.FruitsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                InMemoryRepositoryConfiguration.class,
                JacksonAutoConfiguration.class
        }
)
class InMemoryFruitsRepositoryTest {

    @Autowired
    FruitsRepository sut;

    @Test
    void when_get_all__it_returns_all_elements_present_in_the_json_source() {
        List<Fruit> all = sut.getAll();

        assertEquals(60, all.size());
    }

    @Test
    void when_find_by_and_exists_a_element_with_the_given_id__it_returns_the_element_that_contains_the_given_id() {
        Optional<Fruit> result = sut.findById("1");

        assertTrue(result.isPresent());
        assertEquals(new Fruit("1", "Durian", "Rich and creamy tropical delight", "Southeast Asia"), result.get());
    }

    @Test
    void when_find_by_and_not_exists_a_element_with_the_given_id__it_returns_empty() {
        Optional<Fruit> result = sut.findById("-999");

        assertTrue(result.isEmpty());
    }

    @Test
    void when_get_the_first_page__it_return_the_expected_page_content() {
        int size = 10;
        int pageNumber = 0;

        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Fruit> result =  sut.getPage(pageable);

        assertNotNull(result);

        assertEquals(6, result.getTotalPages());
        assertEquals(60, result.getTotalElements());
        assertEquals(size, result.getSize());
        assertEquals(pageable, result.getPageable());
        Fruit first = result.getContent().get(0);
        assertEquals("1", first.id());
        Fruit last = result.getContent().get(result.getContent().size() - 1);
        assertEquals("10", last.id());
    }

    @Test
    void when_get_the_last_page__it_return_the_expected_page_content() {
        int size = 10;
        int pageNumber = 5;

        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Fruit> result =  sut.getPage(pageable);

        assertNotNull(result);

        assertEquals(6, result.getTotalPages());
        assertEquals(60, result.getTotalElements());
        assertEquals(size, result.getSize());
        assertEquals(pageable, result.getPageable());
        Fruit first = result.getContent().get(0);
        assertEquals("401", first.id());
        Fruit last = result.getContent().get(result.getContent().size() - 1);
        assertEquals("502", last.id());
    }

    @Test
    void when_get_the_last_no_even_size_page__it_return_the_expected_page_content() {
        int size = 7;
        int pageNumber = 8;

        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Fruit> result =  sut.getPage(pageable);

        assertNotNull(result);

        assertEquals(9, result.getTotalPages());
        assertEquals(60, result.getTotalElements());
        assertEquals(size, result.getSize());
        assertEquals(pageable, result.getPageable());
        Fruit first = result.getContent().get(0);
        assertEquals("407", first.id());
        Fruit last = result.getContent().get(result.getContent().size() - 1);
        assertEquals("502", last.id());
        assertEquals(4, result.getNumberOfElements()); // Elements in the current page
    }

}