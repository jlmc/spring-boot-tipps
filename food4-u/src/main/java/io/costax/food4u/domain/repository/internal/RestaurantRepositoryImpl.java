package io.costax.food4u.domain.repository.internal;

import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.RestaurantQueries;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * If we need to implement this custom Impl in a different package, which is not a child of the package than the original repository,
 * then it is necessary to annotate the class with the @Repository annotation required.
 */
@Repository
public class RestaurantRepositoryImpl implements RestaurantQueries {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<Restaurant> searchCustom(final String name, final String cooker) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Restaurant> query = cb.createQuery(Restaurant.class);
        final Root<Restaurant> from = query.from(Restaurant.class);
        from.fetch("cooker", JoinType.LEFT);

        return em.createQuery(query.where(new Predicate[0])).getResultList();
    }
}
