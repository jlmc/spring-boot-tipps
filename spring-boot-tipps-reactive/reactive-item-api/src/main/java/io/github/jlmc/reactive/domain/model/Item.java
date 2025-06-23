package io.github.jlmc.reactive.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor


@Document
public class Item {
    @Id
    private String id;
    @NotBlank(message = "Description is mandatory")
    @Size(max = 100, message = "Description can be at most 100 characters")
    private String description;
    @Positive
    @NotNull
    private Double price;
}
