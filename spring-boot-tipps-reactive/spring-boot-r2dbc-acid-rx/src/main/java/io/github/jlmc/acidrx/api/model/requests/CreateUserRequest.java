package io.github.jlmc.acidrx.api.model.requests;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(@NotBlank String name) {
}
