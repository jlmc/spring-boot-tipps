package io.github.jlmc.cwr.service.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public record PlayerRepresentation(Long id,
                                   @NotBlank String name,
                                   @NotNull LocalDate birthdate,
                                   @NotBlank String countryCode) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;



}
