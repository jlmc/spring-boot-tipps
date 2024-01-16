package io.github.jlmc.doc4poc.service.infrastructure.messaging;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class OrderChanged {
    private String code;
    private String status;
    private String productCode;

}
