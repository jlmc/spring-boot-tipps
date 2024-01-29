package io.github.jlmc.xsgoa.domain.entities;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@Valid
@Document(indexName = "customers")
public class Customer {
    @Id
    private String id;
    @NotNull
    @Positive(message = "Customer number cannot be null")
    private Long number;

    @Builder.Default
    private int origin = 1;

    @Builder.Default
    private List<Account> accounts = new ArrayList<>();
}
