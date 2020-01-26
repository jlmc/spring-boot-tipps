package io.costax.food4u.api.openapi.controllers;

import io.costax.food4u.api.exceptionhandler.Problem;
import io.costax.food4u.api.model.paymentmethods.output.PaymentMethodOutputRepresentation;
import io.costax.food4u.api.model.restaurants.input.RestaurantInputRepresentation;
import io.costax.food4u.api.model.restaurants.output.RestaurantOutputRepresentation;
import io.swagger.annotations.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(tags = "Restaurants")
public interface RestaurantResourcesOpenApi {

    @ApiOperation("Get List of all Restaurants")
    ResponseEntity<List<RestaurantOutputRepresentation>> list();

    @ApiOperation(value = "Get Restaurant by ID", code = 200)
    @ApiResponses({
            @ApiResponse(code = 400, message = "Invalid Restaurant ID", response = Problem.class),
            @ApiResponse(code = 404, message = "Restaurant not found", response = Problem.class)
    })
    RestaurantOutputRepresentation getById(
            @ApiParam(value = "Restaurant ID", required = true, example = "1") Long id);

    @ApiOperation(value = "Create Restaurant", code = 201)
    @ApiResponses({
            @ApiResponse(code = 201, message = "The created Restaurant")
    })
    ResponseEntity<?> add(
            @ApiParam(
                    name = "payload",
                    value = "Restaurant Input Representation",
                    required = true)
                    RestaurantInputRepresentation payload);

    @ApiOperation(value = "Update Restaurant", code = 200)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Restaurant updated"),
            @ApiResponse(code = 404, message = "Restaurant not found", response = Problem.class)
    })
    RestaurantOutputRepresentation update(
            @ApiParam(value = "Restaurant ID", example = "1", required = true)
                    Long restaurantId,
            @ApiParam(
                    value = "Restaurant representation with new datos",
                    name = "payload",
                    required = true)
                    RestaurantInputRepresentation payload);

    @ApiOperation(value = "Update some restaurant properties", code = 200)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Restaurant updated"),
            @ApiResponse(code = 404, message = "Restaurant not found", response = Problem.class)
    })
    RestaurantOutputRepresentation partialUpdate(
            @ApiParam(value = "Restaurant ID", required = true, example = "1") Long restaurantId,
            @ApiParam(value = "Restaurant properties that should be updated",
                    name = "payload",
                    required = true,
                    example = "{ \"name\": \"Simple food\"}") Map<String, Object> payload,
            HttpServletRequest request);

    @ApiOperation(value = "Active the restaurant", code = 204)
    @ApiResponses({
            @ApiResponse(code = 204, message = "Accepted and executed operation"),
            @ApiResponse(code = 404, message = "Restaurant not found", response = Problem.class)
    })
    void ativar(@ApiParam(value = "Restaurant ID", required = true, example = "1") Long restaurantId);

    @ApiOperation(value = "Inactive the restaurant", code = 204)
    @ApiResponses({
            @ApiResponse(code = 204, message = "Accepted and executed operation"),
            @ApiResponse(code = 404, message = "Restaurant not found", response = Problem.class)
    })
    void inactivate(@ApiParam(value = "Restaurant ID", required = true, example = "1") Long restaurantId);

    @ApiOperation(value = "Get Restaurant Payments Methods")
    @ApiResponses({
            @ApiResponse(code = 404, message = "Restaurant not found", response = Problem.class)
    })
    CollectionModel<PaymentMethodOutputRepresentation> getRestaurantPaymentMethods(@ApiParam(value = "Restaurant ID", required = true, example = "1") Long restaurantId);

    @ApiOperation(value = "Add Restaurant Payment Method", code = 204)
    @ApiResponses({
            @ApiResponse(code = 404, message = "Resource not found", response = Problem.class),
            @ApiResponse(code = 204, message = "Payment method added with success")
    })
    void addPaymentMethod(@ApiParam(value = "Restaurant ID", required = true, example = "1") Long restaurantId,
                          @ApiParam(value = "Payment method ID", required = true, example = "1") Long paymentMethodId);

    @ApiOperation(value = "Remove Restaurant Payment Method", code = 204)
    @ApiResponses({
            @ApiResponse(code = 404, message = "Resource not found", response = Problem.class),
            @ApiResponse(code = 204, message = "Payment method removed with success")
    })
    void removePaymentMethod(@ApiParam(value = "Restaurant ID", required = true, example = "1") Long restaurantId,
                             @ApiParam(value = "Payment method ID", required = true, example = "1") Long paymentMethodId);

    /**
     * <code>
     * curl --location --request PUT 'http://localhost:8080/restaurants/activations' \
     * --header 'Content-Type: application/json' \
     * --data-raw '[ 1, 2 ]'
     * </code>
     */
    @ApiOperation(value = "Active multiple Restaurants in Bulk", code = 204)
    @ApiResponse(code = 204, message = "Restaurants activated with success")
    void activeMultiples(
            @ApiParam(value = "Restaurant IDs", example = "[ 1, 2 ]", required = true)
                    List<Long> restaurantIds);

}
