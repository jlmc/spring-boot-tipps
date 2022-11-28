package io.costax.food4u.api.model.restaurants.input;

import io.costax.food4u.core.validation.FileContentType;
import io.costax.food4u.core.validation.FileSize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel(value = "ProductPhotoInput", description = "Product Photo Input Representation")
public class ProductPhotoInputRepresentation {

    @ApiModelProperty(hidden = true)
    @NotNull
    @FileSize(max = "20MB")
    @FileContentType(allowed = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    private MultipartFile file;

    @ApiModelProperty(value = "Product Photo description", required = true)
    private String description;
}
