package io.github.jlmc.cwr.service.api.clubs.requests;

import jakarta.validation.constraints.NotBlank;

public record CreateClubRequest(@NotBlank String name) {
}
