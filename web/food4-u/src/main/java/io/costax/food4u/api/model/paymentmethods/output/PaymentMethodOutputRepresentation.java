package io.costax.food4u.api.model.paymentmethods.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "paymentMethods")
@ApiModel(value = "PaymentMethodOutput")
@Getter
@Setter
public class PaymentMethodOutputRepresentation extends RepresentationModel<PaymentMethodOutputRepresentation> {

    @ApiModelProperty(example = "1")
    private Long id;
    @ApiModelProperty(example = "ADMIN")
    private String name;

}
