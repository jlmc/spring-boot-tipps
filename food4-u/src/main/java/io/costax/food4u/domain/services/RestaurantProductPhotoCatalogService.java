package io.costax.food4u.domain.services;

import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.model.Photo;
import io.costax.food4u.domain.model.Product;
import io.costax.food4u.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantProductPhotoCatalogService {

    private final RestaurantRepository restaurantRepository;
    private PhotoStorageService photoStorageService;

    public RestaurantProductPhotoCatalogService(final RestaurantRepository restaurantRepository, PhotoStorageService photoStorageService) {
        this.restaurantRepository = restaurantRepository;
        this.photoStorageService = photoStorageService;
    }

    @Transactional
    public Photo updatePhoto(Long restaurantId, Long productId, Photo photo) {
        Product product = restaurantRepository
                .findProduct(restaurantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        Product.class,
                        productId,
                        String.format("Product with the identifier %d not found in the restaurant %d", productId, restaurantId)));

        photo.setProduct(product);

        String storagePath = photoStorageService.storage(PhotoStorageService.PhotoStream
                .builder()
                .name(photo.getFileName())
                .inputStream(photo.getInputStream())
                .build());

        photo.setPath(storagePath);
        return restaurantRepository.saveProductPhoto(photo);
    }
}
