package io.costax.food4u.domain.repository;

import io.costax.food4u.domain.model.Request;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestRepository extends CustomJpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {

    @Query("""
            select distinct r
            from Request r
            left join fetch r.client
            left join fetch r.paymentMethod
            left join fetch r.restaurant
            left join fetch r.items i
            left join fetch i.product
            where r.code = :code
            """)
    Optional<Request> getByCodeWithAllInformation(String code);
}
