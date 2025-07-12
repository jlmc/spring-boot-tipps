package io.github.jlmc.demo.kafka.shareddomain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class OrderBookedEvent {
    private String id;
    private String address;
    private String item;
}
