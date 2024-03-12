package io.github.jlmc.cwr.service.common;

import jakarta.validation.constraints.NotBlank;

public record ClubRepresentation(
        Long id,
        @NotBlank
        String name
) {
}
