package io.github.jlmc.acidrx.api.model.requests;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(@NotBlank String name, String notes) {
}
