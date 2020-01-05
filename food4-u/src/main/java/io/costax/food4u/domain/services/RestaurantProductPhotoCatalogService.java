package io.costax.food4u.domain.services;

import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.model.Photo;
import io.costax.food4u.domain.model.Product;
import io.costax.food4u.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Optional;

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


        Optional<Photo> existingPhoto = restaurantRepository.getProductPhoto(restaurantId, productId);
        if (existingPhoto.isPresent()) {
            existingPhoto.map(Photo::getStoredName).ifPresent(photoStorageService::remove);
            restaurantRepository.removeProductPhoto(restaurantId, productId);
        }

        photo.setProduct(product);

        PhotoStorageService.PhotoStream photoStream = PhotoStorageService.PhotoStream
                .builder()
                .name(photo.getFileName())
                .inputStream(photo.getInputStream())
                .contentType(photo.getContentType())
                .build();

        String storagePath = photoStorageService.storage(photoStream);

        photo.setStoredName(storagePath);
        return restaurantRepository.saveProductPhoto(photo);
    }

    public Photo getProductPhoto(final Long restaurantId, final Long productId) {
        return restaurantRepository.getProductPhoto(restaurantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        Photo.class, productId, String.format("The is no Photo in Product %d in the restaurant %d", productId, restaurantId)
                ));
    }

    public InputStream getProductPhoto(String fileName) {
        return photoStorageService.getFile(fileName).getInputStream();
    }

    @Transactional
    public void remove(final Long restaurantId, final Long productId) {
        Optional<Photo> existingPhoto = restaurantRepository.getProductPhoto(restaurantId, productId);
        if (existingPhoto.isPresent()) {
            existingPhoto.map(Photo::getStoredName).ifPresent(photoStorageService::remove);
            restaurantRepository.removeProductPhoto(restaurantId, productId);
        }
    }
}
