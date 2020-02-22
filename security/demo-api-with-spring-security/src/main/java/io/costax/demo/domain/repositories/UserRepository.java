package io.costax.demo.domain.repositories;

import io.costax.demo.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select distinct u from User u left join fetch u.permissions order by u.id")
    @QueryHints({
            @QueryHint(name = org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH, value = "false")
    })
    List<User> getUsersWithPermissions();

    @Query("select distinct u from User u left join fetch u.permissions where u.id = :id")
    @QueryHints({@QueryHint(name = org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH, value = "false")})
    Optional<User> getByIdWithPeAndPermissions(Integer id);


}
