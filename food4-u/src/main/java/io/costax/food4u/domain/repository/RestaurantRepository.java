package io.costax.food4u.domain.repository;

import io.costax.food4u.domain.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Override
    @Query("from Restaurant r left join fetch r.cooker order by r.id desc")
    List<Restaurant> findAll();

    List<Restaurant> findByName(String name);
}
