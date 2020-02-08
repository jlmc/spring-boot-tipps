package io.costax.food4u.domain.repository;

import io.costax.food4u.domain.filters.CookerFilter;
import io.costax.food4u.domain.model.Cooker;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification is DDD Design pattern
 * Encapsulate a Constraint/Predicate and can be concatenated with other Specifications
 */
public final class CookerSpecifications {


    public static Specification<Cooker> withFilters(final CookerFilter requestFilter) {
        return (Specification<Cooker>) (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (requestFilter.getName() != null && !requestFilter.getName().isBlank()) {
                final String pattern = "%" + requestFilter.getName().trim().toLowerCase() + "%";

                final Expression<String> literal = criteriaBuilder.literal(pattern);

                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), literal));
            }


            return criteriaBuilder.and();
        };


    }
}
