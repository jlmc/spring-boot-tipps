package io.costax.food4u.api.model.restaurants.output;

import io.costax.food4u.api.model.cookers.output.CookerOutputRepresentation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;

@Relation(collectionRelation = "restaurants")
@ApiModel(value = "RestaurantOutput")
@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantOutputRepresentation extends RepresentationModel<RestaurantOutputRepresentation> {

    @ApiModelProperty(example = "1")
    private Long id;

    @ApiModelProperty(example = "Quinta Dona Maria")
    private String name;

    @ApiModelProperty(example = "3.15")
    private BigDecimal takeAwayTax = BigDecimal.ZERO;

    private CookerOutputRepresentation cooker;

    private AddressOutputRepresentation address;

    @ApiModelProperty(example = "true")
    private boolean active;

}
