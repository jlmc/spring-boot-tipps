package io.github.jlmc.xsgoa.domain.entities;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

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
