package io.github.jlmc.xsgoa.api;

import io.github.jlmc.xsgoa.domain.dtos.ClientData;
import io.github.jlmc.xsgoa.domain.repositories.CustomerElasticSearchRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@RestController
@RequestMapping("/api/customers")
public class CustomersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomersController.class);


    private final CustomerElasticSearchRepository customerElasticSearchRepository;


    @GetMapping(path = "/_search")
    public ClientData getClientData(@RequestParam("clientNumber") String clientNumber,
                                    @RequestParam("accountNumber") String accountNumber) {
        LOGGER.info("Getting Client Data.");

        return customerElasticSearchRepository.find(clientNumber, accountNumber);
    }


}
