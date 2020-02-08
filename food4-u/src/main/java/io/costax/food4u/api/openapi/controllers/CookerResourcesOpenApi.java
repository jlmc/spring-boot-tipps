package io.costax.food4u.api.openapi.controllers;

import io.costax.food4u.api.exceptionhandler.Problem;
import io.costax.food4u.api.model.CookersXmlWrapper;
import io.costax.food4u.api.model.cookers.input.CookerInputRepresentation;
import io.costax.food4u.api.model.cookers.output.CookerOutputRepresentation;
import io.costax.food4u.domain.filters.CookerFilter;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@Api(tags = "Cookers")
public interface CookerResourcesOpenApi {

    @ApiOperation("Search all Cooker by the name")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "Property names to filter in the response",
                    name = "filter", paramType = "query", type = "string")
    })
    PagedModel<CookerOutputRepresentation> search(CookerFilter filter, @PageableDefault(size = 10, page = 0) Pageable pageable);

    @ApiOperation("Get all cookers")
    CollectionModel<CookerOutputRepresentation> list();

    //@ApiIgnore
    @ApiOperation("Get all cookers in xml")
    CookersXmlWrapper listXml();

    @ApiOperation("Get cooker by id")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Invalid Cooker ID", response = Problem.class),
            @ApiResponse(code = 404, message = "Cooker not found", response = Problem.class)
    })
    CookerOutputRepresentation getById(
            @ApiParam(value = "Cooker ID", example = "1")
                    Long cookerId);


    @ApiOperation("Create new cooker")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Cooker Created"),
    })
    ResponseEntity<CookerOutputRepresentation> add(
            @ApiParam(name = "body", value = "Cooker Input Representation")
                    CookerInputRepresentation payload,
            UriComponentsBuilder b);


    @ApiOperation("Update cookers")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Updated Cooker"),
            @ApiResponse(code = 404, message = "Cooker not found", response = Problem.class)
    })
    ResponseEntity<CookerOutputRepresentation> update(
            @ApiParam(value = "Cooker ID", example = "1") Long cookerId,
            CookerInputRepresentation payload);

    @ApiOperation("Delete cooker")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Cooker deleted"),
            @ApiResponse(code = 404, message = "Cooker not found", response = Problem.class)
    })
    ResponseEntity<?> remover(
            @ApiParam(value = "Cooker ID", example = "1") Long cookerId);
}
