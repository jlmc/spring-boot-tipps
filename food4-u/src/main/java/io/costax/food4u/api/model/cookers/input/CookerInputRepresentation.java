package io.costax.food4u.api.model.cookers.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "CookerInput", description = "Cooker Input Representation")
@Getter
@Setter
public class CookerInputRepresentation {

    //@JsonProperty("title")
    @NotBlank
    @ApiModelProperty(example = "Emanuel Ferro", required = true)
    private String title;
}
