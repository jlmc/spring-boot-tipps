package io.costax.demo.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PasswordInputRepresentation {

    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;
}

