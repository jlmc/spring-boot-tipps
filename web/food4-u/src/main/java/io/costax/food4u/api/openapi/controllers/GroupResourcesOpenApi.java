package io.costax.food4u.api.openapi.controllers;

import io.costax.food4u.api.exceptionhandler.Problem;
import io.costax.food4u.api.model.groups.output.GroupOutputRepresentation;
import io.swagger.annotations.*;
import org.springframework.hateoas.CollectionModel;

@Api(tags = "Groups")
public interface GroupResourcesOpenApi {

    @ApiOperation("Get all groups")
    CollectionModel<GroupOutputRepresentation> list();

    @ApiOperation("Get Groups by id")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Invalid Group ID", response = Problem.class),
            @ApiResponse(code = 404, message = "Group not found", response = Problem.class)
    })
    GroupOutputRepresentation getById(@ApiParam(value = "Group ID", example = "1") Long id);
}
