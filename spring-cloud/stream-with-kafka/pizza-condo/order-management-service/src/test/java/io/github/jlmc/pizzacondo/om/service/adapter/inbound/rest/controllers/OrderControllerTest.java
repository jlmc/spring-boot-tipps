package io.github.jlmc.pizzacondo.om.service.adapter.inbound.rest.controllers;

import io.github.jlmc.pizzacondo.om.service.adapter.inbound.rest.model.OrderRequest;
import io.github.jlmc.pizzacondo.om.service.application.port.input.PlaceOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import io.github.jlmc.pizzacondo.om.service.domain.model.OrderStatus;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static io.github.jlmc.pizzacondo.om.service.Json.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class) // Replace YourController
public class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PlaceOrderUseCase placeOrderUseCase;

    @Test
    public void when_place_an_order_with_valid_data_then_accepted() throws Exception {

        OrderRequest orderRequest =
                new OrderRequest("customer-id-1234", 3, List.of("CHEESE", "PEPPERONI", "JALAPENO"));
        Order order = Mockito.mock(Order.class);

        when(order.getId()).thenReturn("order-id-1234");
        when(order.getStatus()).thenReturn(OrderStatus.RECEIVED);

        when(placeOrderUseCase.placeOrder(orderRequest.toCommand())).thenReturn(order);

        mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(orderRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        status().isAccepted(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.orderId", Is.is("order-id-1234")),
                        jsonPath("$.status", Is.is("RECEIVED"))
                );
    }

    @Test
    void when_place_an_order_with_invalid_data_then_400() throws Exception {
        OrderRequest orderRequest = new OrderRequest(" ", 0, List.of());

        mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(orderRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(
                                """
                                        {
                                          "type": "about:blank",
                                          "title": "Bad Request",
                                          "status": 400,
                                          "detail": "Invalid request content.",
                                          "instance": "/orders",
                                          "errors": [
                                            {
                                              "field": "customerId",
                                              "message": "must not be blank"
                                            },
                                            {
                                              "field": "toppings",
                                              "message": "size must be between 1 and 10"
                                            },
                                            {
                                              "field": "size",
                                              "message": "must be greater than 0"
                                            }
                                          ]
                                        }
                                        """
                        )
                );
    }
}