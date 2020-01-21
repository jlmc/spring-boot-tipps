package io.costax.food4u.api.model.restaurants.output;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "PhotoProduct")
@Getter
@Setter
public class PhotoProductRepresentation {

    private Long id;

    private String fileName;
    private String description;
    private String contentType;
    private Integer size;
}
