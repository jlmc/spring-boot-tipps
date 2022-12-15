package io.github.jlmc.korders.processor.shareddomain.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderEvent {

    private String orderId;

    @Builder.Default
    private Type type = Type.CREATED;

    private Instant instant;

    @Builder.Default
    private List<Item> items = new ArrayList<>();

    public enum Type {
        CREATED,
        UPDATED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class Item {
        private String productId;
        private Integer qty;
    }
}
