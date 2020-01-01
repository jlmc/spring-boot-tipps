package io.costax.food4u.api.model.restaurants.input;

import io.costax.food4u.core.validation.FileContentType;
import io.costax.food4u.core.validation.FileSize;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductPhotoInputRepresentation {

    @NotNull
    @FileSize(max = "500KB")
    @FileContentType(allowed = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    private MultipartFile file;
    private String description;
}
