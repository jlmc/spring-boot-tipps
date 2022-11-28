package io.costax.food4u.api;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.restaurants.input.ProductPhotoInputRepresentation;
import io.costax.food4u.api.model.restaurants.output.PhotoProductRepresentation;
import io.costax.food4u.api.openapi.controllers.RestaurantProductPhotoSubResourceOpenApi;
import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.model.Photo;
import io.costax.food4u.domain.services.RestaurantProductPhotoCatalogService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        path = "/restaurants/{restaurantId}/products/{productId}/photo"
        //produces = MediaType.APPLICATION_JSON_VALUE
)
public class RestaurantProductPhotoSubResource implements RestaurantProductPhotoSubResourceOpenApi {

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

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long restaurantId,
                       @PathVariable Long productId) {
        restaurantProductPhotoCatalogService.remove(restaurantId, productId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PhotoProductRepresentation getPhoto(@PathVariable Long restaurantId,
                                               @PathVariable Long productId) {

        Photo photo = restaurantProductPhotoCatalogService.getProductPhoto(restaurantId, productId);
        return assembler.toRepresentation(photo);
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> getPhoto(@PathVariable Long restaurantId,
                                                        @PathVariable Long productId,
                                                        @RequestHeader(name = "accept") String acceptHeader)
            throws HttpMediaTypeNotAcceptableException {
        try {

            Photo photo = restaurantProductPhotoCatalogService.getProductPhoto(restaurantId, productId);

            MediaType mediaTypePhoto = MediaType.parseMediaType(photo.getContentType());
            List<MediaType> acceptableMediaTypes = MediaType.parseMediaTypes(acceptHeader);

            verifyMediaTypeCompatibilities(mediaTypePhoto, acceptableMediaTypes);

            InputStream inputStream = restaurantProductPhotoCatalogService.getProductPhoto(photo.getStoredName());

            return ResponseEntity.ok()
                    .contentType(mediaTypePhoto)
                    .body(new InputStreamResource(inputStream));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private void verifyMediaTypeCompatibilities(MediaType mediaTypePhoto,
                                                List<MediaType> mediaTypesAceitas) throws HttpMediaTypeNotAcceptableException {

        boolean isCompatible = mediaTypesAceitas
                .stream()
                .anyMatch(acceptableMediaType -> acceptableMediaType.isCompatibleWith(mediaTypePhoto));

        if (!isCompatible) {
            throw new HttpMediaTypeNotAcceptableException(mediaTypesAceitas);
        }
    }

}
