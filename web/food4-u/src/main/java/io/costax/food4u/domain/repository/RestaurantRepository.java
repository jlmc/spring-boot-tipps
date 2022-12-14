package io.costax.food4u.domain.repository;

import io.costax.food4u.domain.model.PaymentMethod;
import io.costax.food4u.domain.model.Product;
import io.costax.food4u.domain.model.Restaurant;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends
        CustomJpaRepository<Restaurant, Long>,
        RestaurantQueries,
        JpaSpecificationExecutor<Restaurant> {

    @Override
    @Query("from Restaurant r left join fetch r.cooker order by r.id desc")
    List<Restaurant> findAll();

    /**
     * The name findByName can have other name, we only need do following some rules for example.
     * Between the keyword 'find' and 'By' we can put any other works, so 'findSomeOtherThingByName' is a valid name.
     * <p>
     * the list of keywords can be found in the documentation:
     *
     * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-keywords">repository-query-keywords</a>
     */
    List<Restaurant> findByName(String name);

    boolean existsByName(String name);

    List<Restaurant> findByTakeAwayTaxBetween(BigDecimal init, BigDecimal until);

    List<Restaurant> findTop2ByTakeAwayTaxBetween(BigDecimal init, BigDecimal until);

    Restaurant findFirstByTakeAwayTaxBetween(BigDecimal init, BigDecimal until);

    /**
     * The query of this method is in a external xml file, with some rules.
     * The name of the Named Query must start with the of the Entity '.' and the exact name of this method
     * So, the for this example we have the NamedQuery: 'Restaurant.findByCookerName'
     */
    //@Query("select r from Restaurant r inner join fetch r.cooker where lower(r.cooker.name) like %:name%")
    List<Restaurant> findByCookerName(@Param("name") String name);

    @QueryHints({
            @QueryHint(name = org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH, value = "false")
    })
    @Query("select distinct pm from Restaurant r inner join r.paymentMethods pm where r.id = :restaurantId order by pm.id")
    List<PaymentMethod> getRestaurantPaymentMethods(Long restaurantId);


    @QueryHints({
            @QueryHint(name = org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH, value = "false")
    })
    @Query("select distinct pm from Restaurant r inner join r.products pm where r.id = :restaurantId order by pm.id")

    List<Product> getRestaurantProducts(Long restaurantId);

    @QueryHints({
            @QueryHint(name = org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH, value = "false"),
            @QueryHint(name = org.hibernate.annotations.QueryHints.FETCH_SIZE, value = "1"),
    })
    @Query("select distinct p from Restaurant r inner join r.products p where r.id = :restaurantId and p.id = :productId ")
    Optional<Product> findProduct(Long restaurantId, Long productId);
}
