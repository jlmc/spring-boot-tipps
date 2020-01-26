package io.costax.food4u.api.model.cookers.output;

import io.costax.food4u.domain.model.Cooker;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@ApiModel(value = "CookerOutput")
@Data
public class CookerOutputRepresentation extends RepresentationModel<CookerOutputRepresentation> {

    @ApiModelProperty(example = "1")
    private Long id;
    @ApiModelProperty(example = "Mario Nabais")
    private String title;

    public static CookerOutputRepresentation of(final Cooker cooker) {
        if (cooker == null) return null;

        CookerOutputRepresentation representation = new CookerOutputRepresentation();
        representation.id = cooker.getId();
        representation.title = cooker.getName();
        return representation;
    }
}
