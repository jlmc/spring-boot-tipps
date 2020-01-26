package io.costax.food4u.api.model.users.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "users")
@ApiModel(value = "UserOutput")
@Getter
@Setter
public class UserOutputRepresentation extends RepresentationModel<UserOutputRepresentation> {

    @ApiModelProperty(example = "1")
    private Long id;
    @ApiModelProperty(example = "John Foo")
    private String name;
    @ApiModelProperty(example = "john.foo@foo-domain.com")
    private String email;
}
