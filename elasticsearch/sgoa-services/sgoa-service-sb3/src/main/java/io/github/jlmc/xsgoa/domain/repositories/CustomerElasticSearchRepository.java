package io.github.jlmc.xsgoa.domain.repositories;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import io.github.jlmc.xsgoa.configurations.elasticsearch.AppElasticsearchConfigurationProperties;
import io.github.jlmc.xsgoa.domain.dtos.ClientData;
import io.github.jlmc.xsgoa.domain.entities.Account;
import io.github.jlmc.xsgoa.domain.entities.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.CriteriaQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

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
     *
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
        CriteriaQuery criteriaQuery = createQueryWithCriteria(clientNumber);
        @SuppressWarnings("unused")
        NativeQuery nativeQuery = createQueryWithNative(clientNumber);

        log.trace("Searching customer account data with customer number {} and account number {}", clientNumber,
                accountNumber);

        SearchHits<Customer> customerSearchHits = elasticsearchOperations.search(criteriaQuery, Customer.class, customersIndexCoordinates);

        List<Customer> queryMatches =
                customerSearchHits
                        .getSearchHits()
                        .stream()
                        .map(SearchHit::getContent)
                        .toList();

        if (queryMatches.isEmpty()) {
            throw new CustomerVisionException("No customer found with the client number '%s' and origin '%s'"
                    .formatted(clientNumber, CUSTOMER_ORIGIN_VALUE));
        }


        var customerAndAccount =
            queryMatches.stream()
                .map(it -> new CustomerAndAccount(it, getCustomerAccount(it, accountNumber)))
                .filter(it -> Objects.nonNull(it.account()))
                .findFirst()
                .orElse(null)
                ;

        if (customerAndAccount == null) {
            var msg = "Account number '%s' for customer with number '%s' does not exist.".formatted(accountNumber, clientNumber);

            log.error(msg);
            throw new CustomerVisionException(msg);
        }

        Account account = customerAndAccount.account();
        Customer customer = customerAndAccount.customer();

        return ClientData.builder()
                .fullAccountNumber(account.getFullNumber())
                .accountNumber(account.getNumber())
                .clientNumber("" + customer.getNumber())
                .accountDueDate(account.getDueDate())
                .renewal(account.getRenewal())
                .build();

    }

    private static Account getCustomerAccount(Customer customer, String accountNumber) {
        return customer.getAccounts()
                .stream()
                .filter(it -> it.getNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    private static CriteriaQuery createQueryWithCriteria(String clientNumber) {
        Criteria matches1 = new Criteria(CLIENT_NUMBER.searchKey).matches(clientNumber);
        Criteria matches2 = new Criteria(ORIGIN.searchKey).matches(CUSTOMER_ORIGIN_VALUE);

        Criteria criteria = matches1.and(matches2);

        return new CriteriaQueryBuilder(criteria).build();
    }

    private NativeQuery createQueryWithNative(String clientNumber) {
        co.elastic.clients.elasticsearch._types.query_dsl.Query build3 = new co.elastic.clients.elasticsearch._types.query_dsl.Query.Builder().match(e -> e.field(CLIENT_NUMBER.searchKey).query(clientNumber)).build();
        co.elastic.clients.elasticsearch._types.query_dsl.Query build4 = new co.elastic.clients.elasticsearch._types.query_dsl.Query.Builder().match(e -> e.field(ORIGIN.searchKey).query(CUSTOMER_ORIGIN_VALUE)).build();

        List<Query> build31 = List.of(build3, build4);

        return NativeQuery.builder()
                .withQuery(q -> q.bool(q1 -> q1.must(build31)))
                .build();
    }

    enum CustomerField {

        ORIGIN("origin"),
        CLIENT_NUMBER("number");

        public final String searchKey;

        CustomerField(String searchKey) {
            this.searchKey = searchKey;
        }
    }

    private record CustomerAndAccount(Customer customer, Account account) {}
}