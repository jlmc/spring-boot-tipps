package io.costax.food4u.domain.repository;

import io.costax.food4u.domain.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Override
    @Query("from Restaurant r left join fetch r.cooker order by r.id desc")
    List<Restaurant> findAll();

    /**
     * The name findByName can have other name, we only need do following some rules for example.
     * Between the keyword 'find' and 'By' we can put any other works, so 'findSomeOtherThingByName' is a valid name.
     *
     * the list of keywords can be found in the documentation:
     *
     * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-keywords">repository-query-keywords</a>
     */
    List<Restaurant> findByName(String name);

    boolean existsByName(String name);

    List<Restaurant> findByTakeAwayTaxBetween(BigDecimal init, BigDecimal until);

    List<Restaurant> findTop2ByTakeAwayTaxBetween(BigDecimal init, BigDecimal until);

    Restaurant findFirstByTakeAwayTaxBetween(BigDecimal init, BigDecimal until);
}
