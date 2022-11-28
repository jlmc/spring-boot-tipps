package io.costax.food4u.domain.repository;

import io.costax.food4u.domain.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CustomJpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByEmailIgnoreCase(String email);
}
