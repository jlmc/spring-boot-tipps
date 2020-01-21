package io.costax.food4u.api.model.users.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "PasswordInput", description = "Password Input representation")
@Getter
@Setter
public class PasswordInputRepresentation {

    @ApiModelProperty(example = "123", required = true)
    @NotBlank
    private String currentPassword;

    @ApiModelProperty(example = "123", required = true)
    @NotBlank
    private String newPassword;
}
