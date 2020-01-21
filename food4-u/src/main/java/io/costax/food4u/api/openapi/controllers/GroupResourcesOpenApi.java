package io.costax.food4u.api.openapi.controllers;

import io.costax.food4u.api.model.groups.output.GroupOutputRepresentation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(tags = "Groups")
public interface GroupResourcesOpenApi {

    @ApiOperation("Get all groups")
    List<GroupOutputRepresentation> list();
}
