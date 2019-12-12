package io.costax.food4u.domain.repository;

import io.costax.food4u.domain.model.Cooker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookerRepository extends JpaRepository<Cooker, Long> {
}
