package io.github.jlmc.xsgoa.domain.repositories;

import io.github.jlmc.xsgoa.domain.dtos.ClientData;
import io.github.jlmc.xsgoa.domain.entities.Customer;
import io.github.jlmc.xsgoa.domain.services.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Testcontainers
@SpringBootTest
class CustomerElasticSearchRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerElasticSearchRepositoryTest.class);

    @Container
    public static ElasticsearchContainer container =
            new ElasticsearchContainer(
                    DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.10.2")
            )
            .withExposedPorts(9200, 9300)
            .withEnv("xpack.security.enabled", "false")
            ;

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        LOGGER.info("Overriding spring properties");
        registry.add("app.elasticsearch.hostAndPort", container::getHttpHostAddress);
    }

    @Autowired
    CustomerElasticSearchRepository customerElasticSearchRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    DataGenerator dataGenerator;

    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    @BeforeEach
    void populateIndexes() {
        LOGGER.info("Populate the customer index");

        List<Customer> customers = dataGenerator.customers();

        customerRepository.deleteAll();
        customerRepository.saveAll(customers);
    }

    @Test
    void searchANExistingElement() {
        LOGGER.info("test find an existing element");
        String clientNumber = "2";
        String accountNumber = "5";

        ClientData clientData = customerElasticSearchRepository.find(clientNumber, accountNumber);

        assertThat(clientData,
                allOf(
                    hasProperty("clientNumber", is(clientNumber)),
                    hasProperty("accountNumber", is(accountNumber)),
                    hasProperty("fullAccountNumber", is("test_2_1"))
                )
        );
    }

    @Test
    void searchNonExistingElement() {
        LOGGER.info("test find non existing element");
        String clientNumber = "999";
        String accountNumber = "5";

        CustomerVisionException exception =
                assertThrows(CustomerVisionException.class, () -> customerElasticSearchRepository.find(clientNumber, accountNumber));

        assertEquals("No customer found with the client number '999' and origin '1'", exception.getMessage());
    }

    @Test
    void searchExistingElementThatDoesNotCountTheAccount() {
        LOGGER.info("test find non existing element");
        String clientNumber = "2";
        String accountNumber = "999";

        CustomerVisionException exception =
                assertThrows(CustomerVisionException.class, () -> customerElasticSearchRepository.find(clientNumber, accountNumber));

        assertEquals("Account number '999' for customer with number '2' does not exist.", exception.getMessage());
    }

}