package io.github.jlmc.orders.interfaces.rest.transform;

import io.github.jlmc.orders.domain.model.commands.CreateOrderCommand;
import io.github.jlmc.orders.domain.model.commands.Item;
import io.github.jlmc.orders.domain.model.commands.UpdateOrderCommand;
import io.github.jlmc.orders.interfaces.rest.domain.OrderRequest;

import java.util.List;

public class OrderCommandDTOAssembler {

    public static CreateOrderCommand toCommandFromDTO(OrderRequest dto) {
        List<Item> items = toItems(dto);

        return new CreateOrderCommand(items);
    }

    public static UpdateOrderCommand toCommandFromDTO(String id, OrderRequest dto) {
        List<Item> items = toItems(dto);

        return new UpdateOrderCommand(id , items);
    }

    private static List<Item> toItems(OrderRequest dto) {
        return dto.getItems()
                  .stream().map(it ->
                        new Item(it.productId(), it.qty())
                ).toList();
    }
}
