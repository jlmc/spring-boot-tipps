package io.github.jlmc.xsgoa.api;

import io.github.jlmc.xsgoa.domain.dtos.ClientData;
import io.github.jlmc.xsgoa.domain.entities.Customer;
import io.github.jlmc.xsgoa.domain.repositories.CustomerElasticSearchRepository;
import io.github.jlmc.xsgoa.domain.repositories.CustomerRepository;
import io.github.jlmc.xsgoa.domain.services.CustomerCostumesIndexPopularService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor

@Tag(name = "Customers", description = "Customers operations.")

@RestController
@RequestMapping("/api/customers")
public class CustomersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomersController.class);

    private final CustomerRepository customerRepository;

    private final CustomerElasticSearchRepository customerElasticSearchRepository;

    private final CustomerCostumesIndexPopularService customerCostumesIndexPopularService;

    @GetMapping
    public List<Customer> getAllCustomers() {
        LOGGER.info("Getting All customers.");

        Iterable<Customer> iterable = customerRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false).toList();
    }


    @Operation(summary = "Search client data in customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the client data", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ClientData.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input supplied", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Client Data not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class))) })
    @GetMapping(path = "/_search")
    public ClientData getClientData(
            @Parameter(description = "customer number", example = "2", required = true) @RequestParam(value = "clientNumber") @NotBlank String clientNumber,
            @Parameter(description = "customer account number", example = "5", required = true) @RequestParam("accountNumber") @NotBlank String accountNumber) {
        LOGGER.info("Getting Client Data.");

        return customerElasticSearchRepository.find(clientNumber, accountNumber);
    }


    @PostMapping("/population-executions")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void executePopulate() {
        LOGGER.info("Scheduling a population-executions.");
        customerCostumesIndexPopularService.populate();
    }
}
