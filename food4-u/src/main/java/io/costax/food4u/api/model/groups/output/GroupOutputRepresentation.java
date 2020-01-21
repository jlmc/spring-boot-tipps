package io.costax.food4u.api.model.groups.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "GroupOutput")
@Getter
@Setter
public class GroupOutputRepresentation {

    @ApiModelProperty(example = "1")
    private Long id;

    @ApiModelProperty(example = "Manager")
    private String name;
}
