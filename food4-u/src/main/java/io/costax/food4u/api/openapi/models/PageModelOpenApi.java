package io.costax.food4u.api.openapi.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageModelOpenApi <T> {

    private List<T> content;

    @ApiModelProperty(example = "10", value = "Number of records per page")
    private Long size;

    @ApiModelProperty(example = "50", value = "Total of elements")
    private Long totalElements;

    @ApiModelProperty(example = "5", value = "Total of pages")
    private Long totalPages;

    @ApiModelProperty(example = "0", value = "Page number (starts at 0)")
    private Long number;

}
