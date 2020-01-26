package io.costax.food4u.api.openapi.controllers;

import io.costax.food4u.api.exceptionhandler.Problem;
import io.costax.food4u.api.model.paymentmethods.input.PaymentMethodInputRepresentation;
import io.costax.food4u.api.model.paymentmethods.output.PaymentMethodOutputRepresentation;
import io.swagger.annotations.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;

@Api(tags = "Payment methods")
public interface PaymentMethodResourcesOpenApi {

    @ApiOperation("List all the payment methods")
    ResponseEntity<CollectionModel<PaymentMethodOutputRepresentation>> list(ServletWebRequest servletWebRequest);

    @ApiOperation("Get Payment Method by ID")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Invalid Payment Method ID", response = Problem.class),
            @ApiResponse(code = 404, message = "Payment Method not found", response = Problem.class)
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<PaymentMethodOutputRepresentation> getById(
            @ApiParam(value = "Payment Method ID", example = "1", required = true) Long id);

    @ApiOperation("Create a new Payment Method")
    @ApiResponses({
            @ApiResponse(code = 201, message = "The created Payment Method"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<?> create(
            @ApiParam(
                    name = "payload",
                    value = "Payment Method Input Representation",
                    required = true)
                    PaymentMethodInputRepresentation payload);
}
