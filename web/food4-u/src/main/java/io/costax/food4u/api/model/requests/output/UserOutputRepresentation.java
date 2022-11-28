package io.costax.food4u.api.model.requests.output;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "RequestUserOutput")
@Setter
@Getter
class UserOutputRepresentation {

    private Long id;
    private String name;
}
