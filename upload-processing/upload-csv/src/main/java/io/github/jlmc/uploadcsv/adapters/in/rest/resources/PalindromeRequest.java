package io.github.jlmc.uploadcsv.adapters.in.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record PalindromeRequest(
        @NotBlank(message = "Text must not be blank") String text
) {
}
