package io.github.jlmc.springbootkafkaexamplerx.sharewddomain;

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
    private String system;
}
