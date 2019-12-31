package io.costax.food4u.domain.repository;

import ch.qos.logback.core.net.server.Client;
import io.costax.food4u.domain.model.Request;
import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.filters.RequestFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification is DDD Design pattern
 * Encapsulate a Constraint/Predicate and can be concatenated with other Specifications
 */
public final class RequestSpecifications {

    public static Specification<Request> withFilters(RequestFilter requestFilter) {
        return (Specification<Request>) (root, query, cb) -> {

            final Join<Request, Restaurant> restaurant = (Join) root.fetch("restaurant", JoinType.LEFT);
            restaurant.fetch("cooker", JoinType.LEFT);
            final Join<Request, Client> client = (Join) root.fetch("client", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            if (requestFilter != null) {
                if (requestFilter.getClientId() != null) {
                    final Predicate equal = cb.equal(client.get("id"), requestFilter.getClientId());
                    predicates.add(equal);
                }

                if (requestFilter.getRestaurantActive() != null) {
                    final Predicate equal = cb.equal(restaurant.get("active"), requestFilter.getRestaurantActive());
                    predicates.add(equal);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


}
