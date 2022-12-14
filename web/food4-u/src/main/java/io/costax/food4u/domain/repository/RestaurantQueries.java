package io.costax.food4u.domain.repository;

import io.costax.food4u.domain.model.Photo;
import io.costax.food4u.domain.model.Restaurant;

import java.util.List;
import java.util.Optional;

/**
 * This interface is a technique to add more power to Spring data Repositories
 * By default the implementations of this interface must have the same name of the original Repository interface with suffix 'Impl'
 *
 * if for some reason we need to change that beaviore we can do using the annotation:
 *
 * <code>
 * @EnableJpaRepositories(repositoryImplementationPostfix = "SomeOtherSuffix")
 * </code>
 *
 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behavior">single repository behavior</a>
 */
public interface RestaurantQueries {

    List<Restaurant> searchCustom(String name, String cooker);

    List<Restaurant> findWithoutTaxAndWithSimilarName(String nome);

    Restaurant getRestaurantOrNotFoundException(final Long restaurantId);

    Restaurant getRestaurantForUpdateOrNotFoundException(final Long restaurantId);

    Photo saveProductPhoto(Photo photo);

    Optional<Photo> findProductPhoto(Long restaurantId, Long productId);

    Optional<Photo> getProductPhoto(Long restaurantId, Long productId);

    void removeProductPhoto(Long restaurantId, Long productId);
}
