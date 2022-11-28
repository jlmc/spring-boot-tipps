package io.costax.food4u.api.openapi.controllers;

import io.costax.food4u.api.exceptionhandler.Problem;
import io.costax.food4u.api.model.restaurants.input.ProductPhotoInputRepresentation;
import io.costax.food4u.api.model.restaurants.output.PhotoProductRepresentation;
import io.swagger.annotations.*;

import java.io.IOException;

@Api(tags = "Restaurants")
public interface RestaurantProductPhotoSubResourceOpenApi {

    @ApiOperation("Update product photo")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Updated Product Photo"),
            @ApiResponse(code = 404, message = "Product or Restaurant not found", response = Problem.class)
    })
    PhotoProductRepresentation updatePhoto(
            @ApiParam(value = "Restaurant ID", example = "1", required = true)
                    Long restaurantId,
            @ApiParam(value = "Product ID", example = "1", required = true)
                    Long productId,
            ProductPhotoInputRepresentation photoInput
    ) throws IOException;


    @ApiOperation("Delete Product Photo of Restaurant")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Photo deleted"),
            @ApiResponse(code = 400, message = "Invalid Restaurant ID", response = Problem.class),
            @ApiResponse(code = 404, message = "Product Photo not found", response = Problem.class)
    })
    void remove(@ApiParam(value = "Restaurant ID", example = "1", required = true) Long restaurantId,
                @ApiParam(value = "Product ID", example = "1", required = true) Long productId);


    @ApiOperation(value = "Get Product Photo",
            produces = "application/json, image/jpeg, image/png")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Invalid Restaurant ID", response = Problem.class),
            @ApiResponse(code = 404, message = "Product Photo not found", response = Problem.class)
    })
    PhotoProductRepresentation getPhoto(@ApiParam(value = "Restaurant ID", example = "1", required = true) Long restaurantId,
                                        @ApiParam(value = "Product ID", example = "1", required = true) Long productId);

//    @ApiOperation(value = "Busca a foto do produto de um restaurante", hidden = true)
//    ResponseEntity<InputStreamResource> getPhoto(@ApiParam(value = "Restaurant ID", example = "1", required = true) Long restaurantId,
//                                                 @ApiParam(value = "Product ID", example = "1", required = true) Long productId,
//                                                 @RequestHeader(name = "accept") String acceptHeader)
//
//            throws HttpMediaTypeNotAcceptableException;
}
