package io.github.jlmc.jackson.serialization.api.books.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

@JsonTest
class BookJsonTest {

    @Autowired
    private JacksonTester<Book> jacksonTester;

    @Test
    void serialiseBook() throws IOException {
        Book book = getBook();

        JsonContent<Book> content = jacksonTester.write(book);

        Assertions.assertEquals(
                """
                        {"id":1234,"title":"Java","isbn":"abc-123","description":"Java 4 ever","author":{"id":1,"name":"Duke"}}
                        """.trim(),
                content.getJson().trim());
    }

    @Test
    void deserialize() throws IOException {
        var json = """
                {"id":1234,"title":"Java","isbn":"abc-123","description":"Java 4 ever","author":{"id":1,"name":"Duke"}}
                """;

        Book object = jacksonTester.parse(json).getObject();

        Assertions.assertEquals(getBook(), object);
    }

    private static Book getBook() {
        Book book = new Book();
        book.setId(1234);
        book.setIsbn("abc-123");
        book.setDescription("Java 4 ever");
        book.setTitle("Java");
        book.setAuthor(Author.of(1, "Duke"));
        return book;
    }
}
