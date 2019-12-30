package io.costax.food4u.domain.repository;

import io.costax.food4u.domain.model.User;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    public static Specification<User> orderByIdAndEmail() {
        return (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("id")), cb.asc(cb.upper(root.get("email"))));
            return cb.conjunction();
        };
    }
}
