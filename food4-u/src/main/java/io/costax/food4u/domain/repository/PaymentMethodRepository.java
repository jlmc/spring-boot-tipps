package io.costax.food4u.domain.repository;

import io.costax.food4u.domain.model.PaymentMethod;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface PaymentMethodRepository extends CustomJpaRepository<PaymentMethod, Long> {

    @Query("select max(p.lastModificationAt) from PaymentMethod p where p.id = :id")
    OffsetDateTime findMaxLastModificationById(Long id);

    @Query("select max(p.lastModificationAt) from PaymentMethod p")
    OffsetDateTime findMaxLastModification();
}
