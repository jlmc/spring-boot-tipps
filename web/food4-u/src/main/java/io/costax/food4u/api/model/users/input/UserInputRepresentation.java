package io.costax.food4u.api.model.users.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@ApiModel(value = "UserInput", description = "User Input Representation")
@Getter
@Setter
public class UserInputRepresentation {

    @ApiModelProperty(example = "John Foo", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(example = "john.foo@gmail.com", required = true)
    @NotBlank
    @Email
    private String email;
}
