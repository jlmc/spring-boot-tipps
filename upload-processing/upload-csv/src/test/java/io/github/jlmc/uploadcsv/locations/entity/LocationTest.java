package io.github.jlmc.uploadcsv.locations.entity;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LocationTest {

    @Test
    void imageUrlRegex() {
        String regex = Location.IMAGE_URL_PATTER;
        Pattern pattern = Pattern.compile(regex);

        assertTrue(pattern.matcher("https://static.nike.com/a/images/f_auto/c5fbda6e-5cb6-4fae-8a05-e47212074dbf/image.jpeg").matches());
        assertTrue(pattern.matcher("https://static.nike.com/image.jpeg").matches());
        assertTrue(pattern.matcher("https://static.nike.com/image.png").matches());
        assertTrue(pattern.matcher("https://static.nike.com/image.jpg").matches());
        assertTrue(pattern.matcher("https://static.nike.com/image.webp").matches());
        assertFalse(pattern.matcher("https://static.nike.com/image.vec").matches());
    }

}
