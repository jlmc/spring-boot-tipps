package io.github.jlmc.uploadcsv.csv.boundary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NullableMustMatchRegexExpressionTest {

    private NullableMustMatchRegexExpression sut;

    @BeforeEach
    void setUp() {
        sut = new NullableMustMatchRegexExpression();
        sut.setParameterString("^[a-z0-9]+@[a-z]+\\.[a-z]{2,3}$");
    }

    @Test
    void onValidValue() {
        assertTrue(sut.isValid("john@example.com"));
        assertTrue(sut.isValid(null));
    }

    @Test
    void onInValidValue() {
        assertFalse(sut.isValid("johnexample.com"));
    }
}
