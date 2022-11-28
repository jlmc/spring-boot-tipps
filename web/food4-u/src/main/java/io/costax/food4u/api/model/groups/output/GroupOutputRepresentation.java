package io.costax.food4u.api.model.groups.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@ApiModel(value = "GroupOutput")
@Relation(collectionRelation = "groups")
@Getter
@Setter
public class GroupOutputRepresentation extends RepresentationModel<GroupOutputRepresentation> {

    @ApiModelProperty(example = "1")
    private Long id;

    @ApiModelProperty(example = "Manager")
    private String name;
}
