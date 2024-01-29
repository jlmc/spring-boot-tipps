package io.github.jlmc.xsgoa.domain.repositories;

import io.github.jlmc.xsgoa.configurations.elasticsearch.AppElasticsearchConfigurationProperties;
import io.github.jlmc.xsgoa.domain.dtos.ClientData;
import io.github.jlmc.xsgoa.domain.entities.Account;
import io.github.jlmc.xsgoa.domain.entities.Customer;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import static io.github.jlmc.xsgoa.domain.repositories.CustomerElasticSearchRepository.CustomerField.CLIENT_NUMBER;
import static io.github.jlmc.xsgoa.domain.repositories.CustomerElasticSearchRepository.CustomerField.ORIGIN;

@Repository
@Slf4j
public class CustomerElasticSearchRepository {
    private static final Integer CUSTOMER_ORIGIN_VALUE = 1;

    private final ElasticsearchOperations elasticsearchOperations;
    private final IndexCoordinates customersIndexCoordinates;

    public CustomerElasticSearchRepository(ElasticsearchOperations elasticsearchOperations, AppElasticsearchConfigurationProperties properties) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.customersIndexCoordinates = IndexCoordinates.of(properties.getCustomersIndex());
    }

    /**
     * The Elastic query should be:
     * <pre>
     * GET customers/_search
     * {
     *   "query": {
     *     "bool": {
     *       "must": [
     *         {
     *           "match": {
     *             "number": "2"
     *           }
     *         },
     *         {
     *           "match": {
     *             "origin": "1"
     *           }
     *         }
     *       ]
     *     }
     *   }
     * }
     * </pre>
     * <p>
     * However, the generated query is using term instead of match:
     *
     * <pre>
     *   GET customers/_search
     *   {
     *   "query": {
     *     "bool": {
     *       "must": [
     *         {
     *           "term": {
     *             "number": {
     *               "value": "2"
     *             }
     *           }
     *         },
     *         {
     *           "term": {
     *             "origin": {
     *               "value": 1
     *             }
     *           }
     *         }
     *       ],
     *       "boost": 1.0
     *     }
     *   },
     *   "version": true,
     *   "sort": []
     * }
     * </pre>
     */
    public ClientData find(String clientNumber, String accountNumber) {
        // "query":{"bool":{"must":[{"term":{"number":{"value":"2"}}},{"term":{"origin":{"value":1}}}],"boost":1.0}}
        final NativeSearchQuery query = buildQueryWithClientNumberAndAccountNumber(clientNumber).build();

        log.trace("Searching customer account data with customer number {} and account number {}", clientNumber,
                accountNumber);

        SearchHits<Customer> customerSearchHits = elasticsearchOperations.search(query, Customer.class, customersIndexCoordinates);

        final Customer customer = customerSearchHits
                .getSearchHits().stream()
                .map(SearchHit::getContent)
                .findFirst()
                .stream()
                .filter(it -> it.getAccounts().stream().anyMatch(account -> account.getNumber()
                        .equals(accountNumber)))
                .findFirst()
                .orElse(null);

        if (customer == null) {
            var msg = String.format("Account number '%s' for customer with number '%s' does not exist.", accountNumber, clientNumber);

            log.error(msg);
            throw new CustomerVisionException(msg);
        }

        final Account account =
                customer.getAccounts()
                        .stream()
                        .filter(it -> it.getNumber().equals(accountNumber))
                        .findFirst()
                        .orElse(null);

        if (account == null) {
            var msg = String.format("Account number '%s' does not exist.", accountNumber);

            log.error(msg);
            throw new CustomerVisionException(msg);
        }

        return ClientData.builder()
                .fullAccountNumber(account.getFullNumber())
                .accountNumber(account.getNumber())
                .clientNumber("" + customer.getNumber())
                .accountDueDate(account.getDueDate())
                .renewal(account.getRenewal())
                .build();
    }

    private NativeSearchQueryBuilder buildQueryWithClientNumberAndAccountNumber(final String clientNumber) {
        return new NativeSearchQueryBuilder().withQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(CLIENT_NUMBER.searchKey, clientNumber))
                .must(QueryBuilders.matchQuery(ORIGIN.searchKey, CUSTOMER_ORIGIN_VALUE)));
    }


    enum CustomerField {
        ORIGIN("origin"),
        CLIENT_NUMBER("number");

        public final String searchKey;

        CustomerField(String searchKey) {
            this.searchKey = searchKey;
        }
    }
}