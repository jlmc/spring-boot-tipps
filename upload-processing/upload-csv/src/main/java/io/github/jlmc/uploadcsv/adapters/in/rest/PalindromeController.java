package io.github.jlmc.uploadcsv.adapters.in.rest;

import io.github.jlmc.uploadcsv.adapters.in.rest.resources.PalindromeRequest;
import io.github.jlmc.uploadcsv.adapters.in.rest.resources.PalindromeResponse;
import io.github.jlmc.uploadcsv.application.services.PalindromicVerifier;
import jakarta.validation.Valid;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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


    @PostMapping("/verify-file")
    public Mono<PalindromeResponse> verifyFile(@RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono
                .flatMap(filePart -> {
                    // Process file content reactively
                    return filePart.content()
                            .map(dataBuffer -> {
                                // Convert DataBuffer to String chunk (be careful with encoding)
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);

                                //dataBuffer.release();
                                DataBufferUtils.release(dataBuffer);

                                return new String(bytes);
                            })
                            // Aggregate all chunks into a single string (not recommended for huge files!)
                            .reduce(String::concat)
                            // Then check palindrome
                            .map(text -> {
                                boolean result = verifier.isPalindrome(text);
                                return new PalindromeResponse(result, result ? "It's a palindrome!" : "Not a palindrome.");
                            });
                });
    }

    @PostMapping("/verify-large-file")
    public Mono<PalindromeResponse> verifyLargeFile(@RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono.publishOn(Schedulers.boundedElastic()).flatMap(filePart -> {
            try {
                Path tempFile = Files.createTempFile("upload-", ".txt");

                return filePart.transferTo(tempFile)
                        .then(Mono.fromCallable(() -> {
                            boolean result = verifier.isPalindromeHighPerformanceFile(tempFile);
                            Files.delete(tempFile);
                            return new PalindromeResponse(result, result ? "It's a palindrome!" : "Not a palindrome.");
                        }));
            } catch (IOException e) {
                return Mono.just(new PalindromeResponse(false, "Error processing file: " + e.getMessage()));
            }
        });
    }

}

