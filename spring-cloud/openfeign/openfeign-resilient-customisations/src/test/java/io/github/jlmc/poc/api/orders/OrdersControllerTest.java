package io.github.jlmc.poc.api.orders;

import io.github.jlmc.poc.configurations.clock.FixedClockConfiguration;
import io.github.jlmc.poc.domain.orders.commands.CreateOrderCommand;
import io.github.jlmc.poc.domain.orders.entities.OrderId;
import io.github.jlmc.poc.domain.orders.ports.incoming.CreateOrderReservation;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrdersController.class)
@Import(FixedClockConfiguration.class)
class OrdersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CreateOrderReservation createOrderReservation;

    @Captor
    ArgumentCaptor<CreateOrderCommand> captor;

    @Test
    void when_create_an_order__it_returns_201() throws Exception {
        when(createOrderReservation.createOrder(any(CreateOrderCommand.class))).thenReturn(new OrderId("order-123"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    {
                                      "productId": "1",
                                      "quantity": 2
                                    }
                                  ]
                                }
                                """)
                )
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"id":"order-123"}
                                """)
                );

        verify(createOrderReservation, times(1)).createOrder(captor.capture());
        CreateOrderCommand command = captor.getValue();
        assertNotNull(command);
        assertEquals(new CreateOrderCommand(Set.of(new CreateOrderCommand.Item("1", 2))), command);
    }

    @Test
    void when_create_an_order_with_empty_items__it_returns_400() throws Exception {
        when(createOrderReservation.createOrder(any(CreateOrderCommand.class))).thenReturn(new OrderId("order-123"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                  ]
                                }
                                """)
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType("application/problem+json"),
                        content().json("""
                        {
                          "type": "about:blank",
                          "title": "Bad Request",
                          "status": 400,
                          "detail": "Invalid request content.",
                          "instance": "/api/orders",
                          "errors": [
                            {
                              "name": "items",
                              "detail": "must not be empty"
                            }
                          ]
                        }
                        """)
                );

        verify(createOrderReservation, never()).createOrder(any());
    }

    @Test
    void when_create_an_order_with_invalid_product_id__it_returns_400() throws Exception {
        when(createOrderReservation.createOrder(any(CreateOrderCommand.class))).thenReturn(new OrderId("order-123"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    {
                                      "productId": "no-integer",
                                      "quantity": 1
                                    }
                                  ]
                                }
                                """)
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType("application/problem+json"),
                        content()
                                .json("""
                                 {
                                   "type": "about:blank",
                                   "title": "Bad Request",
                                   "status": 400,
                                   "detail": "Invalid request content.",
                                   "instance": "/api/orders",
                                   "errors": [
                                     {
                                       "name": "items[0].productId",
                                       "detail": "must match \\"^\\\\d+$\\""
                                     }
                                   ]
                                 }
                                 """)

                );

        verify(createOrderReservation, never()).createOrder(any());
    }

    @Test
    void when_create_an_order_with_invalid_quantity__it_returns_400() throws Exception {
        when(createOrderReservation.createOrder(any(CreateOrderCommand.class))).thenReturn(new OrderId("order-123"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    {
                                      "productId": "1",
                                      "quantity": 0
                                    }
                                  ]
                                }
                                """)
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType("application/problem+json"),
                        content()
                                .json("""
                                 {
                                   "type": "about:blank",
                                   "title": "Bad Request",
                                   "status": 400,
                                   "detail": "Invalid request content.",
                                   "instance": "/api/orders",
                                   "errors": [
                                     {
                                       "name": "items[0].quantity",
                                       "detail": "must be greater than 0"
                                     }
                                   ]
                                 }
                                 """, true)

                );

        verify(createOrderReservation, never()).createOrder(any());
    }


    @Test
    void when_create_an_order_with_mal_formated_quantity__it_returns_400() throws Exception {
        when(createOrderReservation.createOrder(any(CreateOrderCommand.class))).thenReturn(new OrderId("order-123"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    {
                                      "productId": "1",
                                      "quantity": "todo"
                                    }
                                  ]
                                }
                                """)
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType("application/problem+json"),
                        content()
                                .json("""
                                 {"type":"about:blank","title":"Bad Request","status":400,"detail":"Failed to read request","instance":"/api/orders"}
                                 """, true)

                );

        verify(createOrderReservation, never()).createOrder(any());
    }
}