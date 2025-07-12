package io.github.jlmc.uploadcsv.adapters.in.rest;

import io.github.jlmc.uploadcsv.adapters.in.rest.resources.PalindromeRequest;
import io.github.jlmc.uploadcsv.adapters.in.rest.resources.PalindromeResponse;
import io.github.jlmc.uploadcsv.application.services.PalindromicVerifier;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("palindromes")
public class PalindromeController {

    private final PalindromicVerifier verifier;

    public PalindromeController(PalindromicVerifier verifier) {
        this.verifier = verifier;
    }

    @PostMapping("/verify")
    public ResponseEntity<PalindromeResponse> verify(@RequestBody @Valid PalindromeRequest request) {
        if (request == null || request.text() == null) {
            return ResponseEntity.badRequest().body(new PalindromeResponse(false, "Text is required."));
        }

        boolean result = verifier.isPalindrome(request.text());

        return ResponseEntity.ok(new PalindromeResponse(result, result ? "It's a palindrome!" : "Not a palindrome."));
    }
}


