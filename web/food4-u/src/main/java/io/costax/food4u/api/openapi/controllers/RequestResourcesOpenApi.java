package io.costax.food4u.api.openapi.controllers;

import io.costax.food4u.api.exceptionhandler.Problem;
import io.costax.food4u.api.model.requests.input.RequestInputRepresentation;
import io.costax.food4u.api.model.requests.output.RequestOutputRepresentation;
import io.costax.food4u.api.model.requests.output.RequestSummaryOutputRepresentation;
import io.costax.food4u.domain.filters.RequestFilter;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;

@Api(tags = "Requests")
public interface RequestResourcesOpenApi {


    /**
     * http://localhost:8080/requests?clientId=1&restaurantActive=true
     * for pagination
     * http://localhost:8080/requests?size=1&page=1&sort=code,desc
     */
    @ApiOperation("Search of Requests")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "Property names to filter in the response, separated by commas",
                    name = "fields", paramType = "query", type = "string")
    })
    Page<RequestSummaryOutputRepresentation> search(final RequestFilter filters,
                                                    @PageableDefault(size = 10, page = 0) Pageable pageable);


    @ApiOperation("Create Restaurant Request")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Restaurant Request created"),
    })
    RequestOutputRepresentation add(@ApiParam(name = "payload", value = "Restaurant Request representation", required = true) RequestInputRepresentation payload);


    @ApiOperation("Get Restaurant Request By Code")
    @ApiResponses({
            @ApiResponse(code = 404, message = "Restaurant Request not found", response = Problem.class)
    })
    RequestOutputRepresentation getByCode(@ApiParam(value = "Restaurant Request code", example = "f9981ca4-5a5e-4da3-af04-933861df3e55", required = true) String code);


    @ApiOperation("Confirm Restaurant Request")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Restaurant Request confirmed with success"),
            @ApiResponse(code = 404, message = "Restaurant Request not found", response = Problem.class)
    })
    ResponseEntity<Void> confirm(
            @ApiParam(
                    value = "Restaurant Request code",
                    example = "f9981ca4-5a5e-4da3-af04-933861df3e55",
                    required = true)
                    String code);


    @ApiOperation("Cancel Restaurant Request")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Restaurant Request canceled with success"),
            @ApiResponse(code = 404, message = "Restaurant Request not found", response = Problem.class)
    })
    ResponseEntity<Void> cancel(
            @ApiParam(
                    value = "Restaurant Request code",
                    example = "f9981ca4-5a5e-4da3-af04-933861df3e55",
                    required = true)
                    String code);


    @ApiOperation("Delivery Restaurant Request")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Restaurant Request Delivered with success"),
            @ApiResponse(code = 404, message = "Restaurant Request not found", response = Problem.class)
    })
    ResponseEntity<Void> delivery(@ApiParam(
            value = "Restaurant Request code",
            example = "f9981ca4-5a5e-4da3-af04-933861df3e55",
            required = true)
                                          String code);


}
