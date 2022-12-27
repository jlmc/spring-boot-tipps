package io.github.jlmc.orders.interfaces.rest.domain;


import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.Instant;
import java.util.List;


@Relation(collectionRelation = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "id")
@Builder
public class OrderRepresentation extends RepresentationModel<OrderRepresentation> {
    private String id;
    private Instant created;
    @Singular
    private List<OrderItem> items;

}
