package io.github.jlmc.orders.interfaces.rest;

import io.github.jlmc.orders.application.internal.commandservices.CreateOrderCommandService;
import io.github.jlmc.orders.application.internal.commandservices.UpdateOrderCommandService;
import io.github.jlmc.orders.application.internal.outboundservices.acl.OrderIdGeneratorService;
import io.github.jlmc.orders.application.internal.queryservices.OrderQueryService;
import io.github.jlmc.orders.domain.model.aggregates.Order;
import io.github.jlmc.orders.interfaces.rest.domain.OrderRequest;
import io.github.jlmc.orders.interfaces.rest.domain.OrderRepresentation;
import io.github.jlmc.orders.interfaces.rest.transform.OrderCommandDTOAssembler;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/orders")
@Validated
public class OrdersController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersController.class);

    private final CreateOrderCommandService createOrderCommandService;
    private final UpdateOrderCommandService updateOrderCommandService;
    private final OrderQueryService orderQueryService;
    private final RepresentationModelAssemblerSupport<Order, OrderRepresentation> orderAssembler;

    @GetMapping(path = "/{id}")
    public ResponseEntity<OrderRepresentation> getOnOrder(@PathVariable String id) {
        LOGGER.info("reading the order with id {}", id);

        return orderQueryService.findById(id)
                                .map(orderAssembler::toModel)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderRepresentation> createOrder(@RequestBody @Validated OrderRequest payload) {
        LOGGER.info("creating a new order with items: {}", payload);

        Order createdOrder = createOrderCommandService.execute(OrderCommandDTOAssembler.toCommandFromDTO(payload));

        OrderRepresentation orderRepresentation = orderAssembler.toModel(createdOrder);

        UriComponents uriComponents =
                fromMethodCall(on(OrdersController.class).getOnOrder(orderRepresentation.getId()))
                        .buildAndExpand(1);

        return ResponseEntity.created(uriComponents.toUri()).body(orderRepresentation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderRepresentation> updateOrder(@PathVariable String id, @RequestBody OrderRequest payload) {
        LOGGER.info("update a existing order with id <{}> with items: {}", id, payload);

        Order updatedOrder =
                updateOrderCommandService.execute(OrderCommandDTOAssembler.toCommandFromDTO(id, payload));

        return ResponseEntity.ok(orderAssembler.toModel(updatedOrder));
    }
}
