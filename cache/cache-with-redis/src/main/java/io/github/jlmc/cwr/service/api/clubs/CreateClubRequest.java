package io.github.jlmc.cwr.service.api.clubs;

import jakarta.validation.constraints.NotBlank;

public record CreateClubRequest(@NotBlank String name) {
}
