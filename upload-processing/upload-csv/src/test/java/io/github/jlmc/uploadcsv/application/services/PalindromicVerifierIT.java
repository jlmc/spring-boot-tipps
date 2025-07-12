package io.github.jlmc.uploadcsv.application.services;

import io.github.jlmc.uploadcsv.adapters.in.rest.resources.PalindromeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import static io.github.jlmc.uploadcsv.Containers.getMongoDBContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureWebTestClient
class PalindromicVerifierIT {

    @Container
    static MongoDBContainer mongoDBContainer = getMongoDBContainer();

    @DynamicPropertySource
    static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void verifyLargePalindromeInput() {
        String largeText = generateLargePalindrome(100); // 100MB
        PalindromeRequest request = new PalindromeRequest(largeText);

        webTestClient.post()
                .uri("/palindrome/verify")
                .body(Mono.just(request), PalindromeRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.palindrome").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("It's a palindrome!");
    }


    private String generateLargePalindrome(int sizeInMB) {
        int targetSizeBytes = sizeInMB * 1024 * 1024;
        String pattern = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(targetSizeBytes / 2);

        while (sb.length() < targetSizeBytes / 2) {
            sb.append(pattern);
        }

        String half = sb.toString();
        String fullPalindrome = half + new StringBuilder(half).reverse();
        return fullPalindrome.substring(0, targetSizeBytes);
    }

}