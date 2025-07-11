package io.github.jlmc.uploadcsv.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PalindromicVerifierTest {

    private PalindromicVerifier verifier;

    @BeforeEach
    void setUp() {
        verifier = new PalindromicVerifier();
    }

    // ✅ Simple palindromes
    @Test
    void shouldReturnTrueForSimplePalindromes() {
        assertTrue(verifier.isPalindrome("madam"));
        assertTrue(verifier.isPalindrome("racecar"));
        assertTrue(verifier.isPalindrome("121"));
    }

    // ✅ Case-insensitive comparison
    @Test
    void shouldIgnoreCaseWhenCheckingPalindrome() {
        assertTrue(verifier.isPalindrome("MadAm"));
        assertTrue(verifier.isPalindrome("RaceCar"));
    }

    // ✅ Palindromes with spaces and punctuation
    @Test
    void shouldIgnoreSpecialCharacters() {
        assertTrue(verifier.isPalindrome("A man, a plan, a canal: Panama"));
        assertTrue(verifier.isPalindrome("No 'x' in Nixon"));
        assertTrue(verifier.isPalindrome("Was it a car or a cat I saw?"));
    }

    // ❌ Non-palindromes
    @Test
    void shouldReturnFalseForNonPalindromes() {
        assertFalse(verifier.isPalindrome("hello"));
        assertFalse(verifier.isPalindrome("OpenAI"));
        assertFalse(verifier.isPalindrome("abcde"));
    }

    // 🈳 Empty and blank strings
    @Test
    void shouldReturnFalseForEmptyOrBlankStrings() {
        assertFalse(verifier.isPalindrome(""));
        assertFalse(verifier.isPalindrome("    "));
    }

    // 🔒 Null input
    @Test
    void shouldReturnFalseForNullInput() {
        assertFalse(verifier.isPalindrome(null));
    }

    // 🧪 Alphanumeric palindromes
    @Test
    void shouldHandleMixedAlphanumericPalindromes() {
        assertTrue(verifier.isPalindrome("A1B2B1a"));
    }

    // ❌ Mismatching characters with ignored specials
    @Test
    void shouldReturnFalseWhenCharactersDoNotMatchIgnoringSpecials() {
        assertFalse(verifier.isPalindrome("abc#cda"));
    }
}