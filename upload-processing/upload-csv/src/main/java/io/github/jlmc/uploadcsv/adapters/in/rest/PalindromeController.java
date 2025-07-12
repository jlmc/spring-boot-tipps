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
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("palindromes")
public class PalindromeController {

    private final PalindromicVerifier verifier;

    public PalindromeController(PalindromicVerifier verifier) {
        this.verifier = verifier;
    }

    @PostMapping("/verify")
    public Mono<ResponseEntity<PalindromeResponse>> verify(@RequestBody @Valid Mono<PalindromeRequest> requestMono) {
        return requestMono
                .map(request -> {
                    String text = request.text();
                    if (text == null) {
                        return ResponseEntity.badRequest()
                                .body(new PalindromeResponse(false, "Text is required."));
                    }

                    boolean isPalindrome = verifier.isPalindrome(text);

                    return ResponseEntity.ok(new PalindromeResponse(isPalindrome, isPalindrome ? "It's a palindrome!" : "Not a palindrome."));
                })
                .onErrorResume(Mono::error);
    }
}

