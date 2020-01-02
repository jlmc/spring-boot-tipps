package io.costax.food4u.api;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.restaurants.input.ProductPhotoInputRepresentation;
import io.costax.food4u.api.model.restaurants.output.PhotoProductRepresentation;
import io.costax.food4u.domain.model.Photo;
import io.costax.food4u.domain.services.RestaurantProductPhotoCatalogService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/restaurants/{restaurantId}/products/{productId}/photo")
public class RestaurantProductPhotoSubResource {

    private final Assembler<PhotoProductRepresentation, Photo> assembler;
    private final RestaurantProductPhotoCatalogService restaurantProductPhotoCatalogService;

    public RestaurantProductPhotoSubResource(final Assembler<PhotoProductRepresentation, Photo> assembler,
                                             final RestaurantProductPhotoCatalogService restaurantProductPhotoCatalogService) {
        this.assembler = assembler;
        this.restaurantProductPhotoCatalogService = restaurantProductPhotoCatalogService;
    }

    @PutMapping(/*value = "/{productId}/photo", */consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PhotoProductRepresentation updatePhoto(@PathVariable Long restaurantId,
                                                  @PathVariable Long productId,

                                                  @Valid ProductPhotoInputRepresentation photoInput) {

        String fileName = UUID.randomUUID().toString() + "_" + photoInput.getFile().getOriginalFilename();

        Photo photo = new Photo();
        photo.setId(productId);
        photo.setDescription(photoInput.getDescription());
        photo.setFileName(fileName);
        photo.setContentType(photoInput.getFile().getContentType());
        photo.setSize(photoInput.getFile().getSize());
        try {
            photo.setInputStream(photoInput.getFile().getInputStream());
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        Photo savedPhoto = restaurantProductPhotoCatalogService.updatePhoto(restaurantId, productId, photo);

        return assembler.toRepresentation(savedPhoto);

    }

}
