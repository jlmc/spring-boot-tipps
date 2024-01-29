package io.github.jlmc.xsgoa.domain.repositories;

import io.github.jlmc.xsgoa.domain.entities.Customer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends ElasticsearchRepository<Customer, String> {

    List<Customer> findByNumber(Long number);

    List<Customer> findCustomerByNumberBetweenOrderByNumber(Long low, Long high);
}
