package io.costax.food4u.api.model.restaurants.output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoProductRepresentation {

    private Long id;

    private String fileName;
    private String description;
    private String contentType;
    private Integer size;
}
