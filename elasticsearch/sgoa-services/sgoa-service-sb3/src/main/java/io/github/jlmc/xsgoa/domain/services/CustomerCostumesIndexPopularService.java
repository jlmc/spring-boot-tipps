package io.github.jlmc.xsgoa.domain.services;

import io.github.jlmc.xsgoa.domain.entities.Customer;
import io.github.jlmc.xsgoa.domain.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@AllArgsConstructor
@Service
public class CustomerCostumesIndexPopularService {

    private final CustomerRepository customerRepository;
    private final DataGenerator dataGenerator;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public void populate() {
        executorService.submit(this::execution);
    }

    private void execution() {
        log.info("Populating the customer index.");
        List<Customer> customers = dataGenerator.customers();

        customerRepository.deleteAll();

        customerRepository.saveAll(customers);

        log.info("Populated the customer index Successful.");
    }
}
