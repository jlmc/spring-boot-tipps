package io.github.jlmc.orders.interfaces.rest.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.stream.Collectors.joining;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    @NotEmpty
    @NotNull
    @Valid
    private List<OrderItem> items;

    @Override
    public String toString() {
        return getItems().stream()
                         .map(OrderItem::toString)
                         .collect(joining(", ", "[", "]"));
    }
}
