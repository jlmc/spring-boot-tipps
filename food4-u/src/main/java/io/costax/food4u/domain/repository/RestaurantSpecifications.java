package io.costax.food4u.domain.repository;

import io.costax.food4u.domain.model.Restaurant;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigDecimal;

/**
 * Specification is DDD Design pattern
 * Encapsulate a Constraint/Predicate and can be concatenated with other Specifications
 */
public final class RestaurantSpecifications {

    public static Specification<Restaurant> withFeeTakeAwayTax() {
        return new Specification<Restaurant>() {
            @Override
            public Predicate toPredicate(final Root<Restaurant> root,
                                         final CriteriaQuery<?> query,
                                         final CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("takeAwayTax"), BigDecimal.ZERO);
            }
        };
    }

    public static Specification<Restaurant> withSimilarName(final String similarName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + similarName + "%s");
    }
}
