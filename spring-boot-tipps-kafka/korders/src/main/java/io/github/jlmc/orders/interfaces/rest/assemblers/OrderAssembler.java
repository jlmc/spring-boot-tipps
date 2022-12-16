package io.github.jlmc.orders.interfaces.rest.assemblers;

import io.github.jlmc.orders.domain.model.aggregates.Order;
import io.github.jlmc.orders.interfaces.rest.OrdersController;
import io.github.jlmc.orders.interfaces.rest.domain.OrderItem;
import io.github.jlmc.orders.interfaces.rest.domain.OrderRepresentation;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderAssembler extends RepresentationModelAssemblerSupport<Order, OrderRepresentation> {
    public OrderAssembler() {
        super(OrdersController.class, OrderRepresentation.class);
    }

    @Override
    public OrderRepresentation toModel(Order entity) {
        OrderRepresentation model =
                new OrderRepresentation(
                        entity.getId(),
                        entity.getCreated(),
                        entity.getOrderItems().stream().map(it -> new OrderItem(it.getProduct().getId(), it.getQty())).toList()
                );

        addLinks(model, entity.getId());

        return model;
    }

    private void addLinks(final OrderRepresentation model, final String id) {
        Link self = linkTo(methodOn(OrdersController.class).getOnOrder(id)).withRel(IanaLinkRelations.SELF);
        Link update = linkTo(methodOn(OrdersController.class).updateOrder(id, null)).withRel("update");

        model.add(self, update);
    }
}
