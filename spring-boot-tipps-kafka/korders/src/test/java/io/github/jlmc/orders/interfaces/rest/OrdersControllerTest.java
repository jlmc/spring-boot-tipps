package io.github.jlmc.orders.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.orders.application.internal.commandservices.CreateOrderCommandService;
import io.github.jlmc.orders.application.internal.commandservices.UpdateOrderCommandService;
import io.github.jlmc.orders.application.internal.outboundservices.acl.OrderIdGeneratorService;
import io.github.jlmc.orders.application.internal.queryservices.OrderQueryService;
import io.github.jlmc.orders.domain.model.aggregates.Order;
import io.github.jlmc.orders.domain.model.entities.Product;
import io.github.jlmc.orders.interfaces.rest.assemblers.OrderAssembler;
import io.github.jlmc.orders.interfaces.rest.domain.OrderItem;
import io.github.jlmc.orders.interfaces.rest.domain.OrderRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static io.github.jlmc.orders.ClassPathResources.fromFile;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest({
        OrdersController.class,
        OrderIdGeneratorService.class,
        ObjectMapper.class,
        OrderAssembler.class
})
@AutoConfigureMockMvc
class OrdersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderQueryService orderQueryService;

    @MockBean
    CreateOrderCommandService createOrderCommandService;

    @MockBean
    UpdateOrderCommandService updateOrderCommandService;

    @Test
    void getOnOrder() throws Exception {

        String s = "1234";
        Instant utc = LocalDate.parse("2022-12-11").atTime(LocalTime.NOON).atZone(ZoneId.of("UTC")).toInstant();
        Order order = Order.builder()
                           .id(s)
                           .created(utc)
                           .orderItems(List.of(io.github.jlmc.orders.domain.model.valueobjects.OrderItem.of(Product.of("1", "I-Phone"), 1)))
                           .build();

        Mockito.when(orderQueryService.findById(Mockito.eq(s))).thenReturn(Optional.of(order));

        mockMvc.perform(get("/v1/orders/{id}", s)
                       .contentType(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    void createOrder() throws Exception {
        byte[] json = fromFile("request-payloads/create-order-request-payload.json");

        OrderRequest orderRequest = this.objectMapper.readValue(json, OrderRequest.class);

        String s = "1234";
        Instant utc = LocalDate.parse("2022-12-11").atTime(LocalTime.NOON).atZone(ZoneId.of("UTC")).toInstant();
        Order order = Order.builder()
                           .id(s)
                           .created(utc)
                           .orderItems(List.of(io.github.jlmc.orders.domain.model.valueobjects.OrderItem.of(Product.of("1", "I-Phone"), 1)))
                           .build();

        Mockito.when(createOrderCommandService.execute(Mockito.any()))
               .thenReturn(order);


        mockMvc.perform(post("/v1/orders")
                       .content(json)
                       .contentType(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(status().isCreated());
    }

    @Test
    void createOrder_4xx() throws Exception {
        OrderRequest orderRequest =
                OrderRequest.builder()
                            .items(List.of(new OrderItem("", 0)))
                            .build();

        String json = objectMapper.writeValueAsString(orderRequest);

        mockMvc.perform(post("/v1/orders")
                       .content(json)
                       .contentType(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrder() throws Exception {
        byte[] json = fromFile("request-payloads/create-order-request-payload.json");

        String s = "1234";
        Instant utc = LocalDate.parse("2022-12-11").atTime(LocalTime.NOON).atZone(ZoneId.of("UTC")).toInstant();
        Order order = Order.builder()
                           .id(s)
                           .created(utc)
                           .orderItems(List.of(io.github.jlmc.orders.domain.model.valueobjects.OrderItem.of(Product.of("1", "I-Phone"), 1)))
                           .build();

        Mockito.when(updateOrderCommandService.execute(Mockito.any()))
               .thenReturn(order);


        mockMvc.perform(put("/v1/orders/{id}", "1234")
                       .content(json)
                       .contentType(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(status().isOk());
    }
}
