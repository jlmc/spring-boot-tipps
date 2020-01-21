package io.costax.food4u.api.openapi.controllers;

import io.costax.food4u.api.exceptionhandler.Problem;
import io.costax.food4u.api.model.restaurants.input.ProductInputRepresentation;
import io.costax.food4u.api.model.restaurants.output.ProductOutputRepresentation;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Api(tags = "Restaurants")
public interface RestaurantProductsSubResourcesOpenApi {

    @ApiOperation("Get List of Product of a Restaurant")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Invalid Restaurant ID", response = Problem.class),
            @ApiResponse(code = 404, message = "Restaurant not found", response = Problem.class)
    })
    List<ProductOutputRepresentation> list(
            @ApiParam(value = "Restaurant ID", required = true, example = "1") Long restaurantId,
            @ApiParam(hidden = true)
            @RequestParam Map<String, String> allParams);


    @ApiOperation("Get Restaurant Product By ID")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Invalid Restaurant or Product ID", response = Problem.class),
            @ApiResponse(code = 404, message = "Restaurant or Product not found", response = Problem.class)
    })
    ProductOutputRepresentation findById(
            @ApiParam(value = "Restaurant ID", required = true, example = "1") Long restaurantId,
            @ApiParam(value = "Product ID", required = true, example = "1") Long productId);


    @ApiOperation("Create a new Product in Restaurant with the ID")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Product created in the restaurant with success"),
            @ApiResponse(code = 404, message = "Restaurant or Product not found", response = Problem.class)
    })
    ProductOutputRepresentation add(
            @ApiParam(value = "Restaurant ID", required = true, example = "1") Long restaurantId,
            @ApiParam(
                    name = "payload",
                    value = "Product Input Representation",
                    required = true) ProductInputRepresentation payload);


    @ApiOperation("Update a existing Product in Restaurant with the ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Product created in the restaurant with success"),
            @ApiResponse(code = 404, message = "Restaurant or Product not found", response = Problem.class)
    })
    ProductOutputRepresentation update(
            @ApiParam(value = "Restaurant ID", required = true, example = "1") Long restaurantId,
            @ApiParam(value = "Product ID", required = true, example = "1") Long productId,
            @ApiParam(
                    name = "payload",
                    value = "Product Input Representation",
                    required = true) ProductInputRepresentation payload);


    @ApiOperation("Delete a existing Product in Restaurant with the ID")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Product deleted in the restaurant with success"),
            @ApiResponse(code = 404, message = "Restaurant or Product not found", response = Problem.class)
    })
    void delete(@ApiParam(value = "Restaurant ID", required = true, example = "1") Long restaurantId,
                @ApiParam(value = "Product ID", required = true, example = "1") Long productId);

}
