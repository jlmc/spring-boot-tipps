package io.costax.food4u.domain.repository.internal;

import io.costax.food4u.domain.exceptions.RestaurantNotFoundException;
import io.costax.food4u.domain.model.Photo;
import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.RestaurantQueries;
import io.costax.food4u.domain.repository.RestaurantRepository;
import io.costax.food4u.domain.repository.RestaurantSpecifications;
import org.hibernate.annotations.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

/**
 * If we need to implement this custom Impl in a different package,
 * which is not a child of the package than the original repository,
 * then it is necessary to annotate the class with the @Repository annotation required.
 */
@Repository
public class RestaurantRepositoryImpl implements RestaurantQueries {

    @PersistenceContext
    EntityManager em;

    @Lazy
    @Autowired
    RestaurantRepository restaurantRepository;

    @Override
    public List<Restaurant> searchCustom(final String name, final String cooker) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Restaurant> query = cb.createQuery(Restaurant.class);
        final Root<Restaurant> from = query.from(Restaurant.class);
        from.fetch("cooker", JoinType.LEFT);

        return em.createQuery(query.where()).getResultList();
    }

    @Override
    public List<Restaurant> findWithoutTaxAndWithSimilarName(final String name) {
        return this.restaurantRepository
                .findAll(
                        RestaurantSpecifications.withFeeTakeAwayTax()
                                .and(RestaurantSpecifications.withSimilarName(name)));
    }

    @Override
    public Restaurant getRestaurantOrNotFoundException(final Long restaurantId) {
        return restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> RestaurantNotFoundException.of(restaurantId));
    }

    @Override
    public Restaurant getRestaurantForUpdateOrNotFoundException(final Long restaurantId) {
        return restaurantRepository
                .findByIdForceIncrement(restaurantId)
                .orElseThrow(() -> RestaurantNotFoundException.of(restaurantId));
    }

    @Override
    @Transactional
    public Photo saveProductPhoto(final Photo photo) {
        Photo merge = em.merge(photo);
        em.flush();
        return merge;
    }

    @Override
    public Optional<Photo> findProductPhoto(final Long restaurantId, final Long productId) {
        return Optional.empty();
    }

    @Override
    public Optional<Photo> getProductPhoto(final Long restaurantId, final Long productId) {

        try {
            Photo photo = em.createQuery(
                    """
                            select distinct ph
                            from Photo ph
                            inner join fetch ph.product p
                            where ph.id = :productId
                            and p.id = :productId
                            and exists (select 1 from Restaurant r inner join r.products ps where ps.id = p.id and r.id = :restaurantId )

                            """,
                    Photo.class)
                    .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                    .setHint(QueryHints.FETCH_SIZE, 1)
                    .setParameter("productId", productId)
                    .setParameter("restaurantId", restaurantId)
                    .getSingleResult();

            return Optional.ofNullable(photo);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void removeProductPhoto(final Long restaurantId, final Long productId) {
        em.createQuery(
                """
                        delete from Photo ph
                        where ph.product.id = :productId
                        and exists (
                            select 1
                            from Restaurant r
                            inner join r.products ps
                            where ps.id = ph.product.id
                            and r.id = :restaurantId
                        )
                        """)
                .setParameter("productId", productId)
                .setParameter("restaurantId", restaurantId)
                .executeUpdate();
    }


}
