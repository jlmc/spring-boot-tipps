package io.xine.authserverswithjwt.domain.repository;

import io.xine.authserverswithjwt.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @QueryHints({
            @QueryHint(name = org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH, value = "false")
    })
    @Query("""
        select distinct u
        from User u
        left join fetch u.groups g
        left join fetch g.permissions
        where upper(trim(u.email)) = upper(trim( :email ))
        """)
    Optional<User> findByEmailIgnoreCase(String email);
}
