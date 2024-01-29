package io.github.jlmc.xsgoa.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientData implements Serializable {

    private String clientNumber;
    private String name;
    private String sequentialNumber;
    private String accountNumber;
    private BigDecimal amount;
    //VIC fields
    private String fullAccountNumber;
    private OffsetDateTime accountDueDate;
    private Boolean renewal;
}
