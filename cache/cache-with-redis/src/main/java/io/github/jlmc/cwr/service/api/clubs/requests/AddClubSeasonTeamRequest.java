package io.github.jlmc.cwr.service.api.clubs.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AddClubSeasonTeamRequest(
        @NotBlank
        String season,
        @NotNull
        @NotEmpty
        Set<Long> players
) {
}
