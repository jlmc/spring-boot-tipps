## Set up Elasticsearch

To proceed, let’s set up and launch Elasticsearch. There are various methods to accomplish this task, you can refer to the official documentation for Elasticsearch setup here: [Set up Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/setup.html)

For the sake of simplicity in this article, we will install Elasticsearch using Docker. Follow the command below to initiate a single-node cluster with Docker.

```shell
docker run --rm -p 9200:9200 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  docker.elastic.co/elasticsearch/elasticsearch:8.10.2
```

or use the [docker-compose.yaml](docker-compose.yaml) file:

```shell
docker-compose up -d
```

Elasticsearch 8 comes with [SSL/TLS enabled by default](https://www.elastic.co/guide/en/elasticsearch/reference/current/release-highlights.html#_security_features_are_enabled_and_configured_by_default), I disabled security with the environment variable `xpack.security.enabled=false`. If security remains enabled, configuring the Elasticsearch client will require setting up a proper SSL connection. I will assign this task as homework for you, just to keep things interesting! 😆

Hits this URL http://localhost:9200/ the result should be as below.

```json
{
  "name" : "706ea0a25977",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "3xyVC_WdQMWkIQE2A9_ZRQ",
  "version" : {
    "number" : "8.10.2",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "6d20dd8ce62365be9b1aca96427de4622e970e9e",
    "build_date" : "2023-09-19T08:16:24.564900370Z",
    "build_snapshot" : false,
    "lucene_version" : "9.7.0",
    "minimum_wire_compatibility_version" : "7.17.0",
    "minimum_index_compatibility_version" : "7.0.0"
  },
  "tagline" : "You Know, for Search"
}
```

## Elasticsearch Client Config
Spring Data Elasticsearch operates upon an Elasticsearch client (provided by Elasticsearch client libraries) that is connected to a single Elasticsearch node or a cluster. In this article, we’ll establish a connection to Elasticsearch using the imperative (non-reactive) client.

```
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableElasticsearchRepositories(basePackages = "io.github.jlmc.sb3elasticsearch.domain")
public class AppElasticsearchClientConfiguration extends ElasticsearchConfiguration {

    private final AppElasticsearchConfigurationProperties properties;

    public AppElasticsearchClientConfiguration(AppElasticsearchConfigurationProperties properties) {
        this.properties = properties;
    }

    @Override
    public ClientConfiguration clientConfiguration() {
        String hostAndPort = "localhost:9200";

        var builder = ClientConfiguration.builder().connectedTo(hostAndPort);

        if (properties.isSecured()) {
            builder.withBasicAuth(properties.getUsername(), properties.getPassword());
        }
        if (properties.isReverseProxy()) {
            builder.withPathPrefix(properties.getPathPrefix());
        }
        if (properties.isHttps()) {
            builder.usingSsl(sslContext());
        }

        return builder.build();
    }

    private SSLContext sslContext() {
        try {
            return SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
```

Several additional types of clients can be configured and connected with. For further details, you can refer to the following links: [Elasticsearch Clients](https://docs.spring.io/spring-data/elasticsearch/reference/#elasticsearch.clients), [Client Configuration](https://docs.spring.io/spring-data/elasticsearch/reference/#elasticsearch.clients.configuration)

## Object Mapping

Spring Data Elasticsearch Object Mapping is the process that maps a Java object into the JSON representation that is stored in Elasticsearch and back.

In our application, we will be working with items that possess properties such as name, price, brand, and category. To store these items as documents in Elasticsearch, we will represent them using a POJO (Plain Old Java Object) as shown below.

```
@Data
@Builder(toBuilder = true)
@Valid
@Document(indexName = "customers")
public class Customer {
    @Id
    private String id;
    @NotNull
    @Positive(message = "Customer number cannot be null")
    private Long number;
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();
}

```

- `@Document`: Indicates this class is a candidate for storing in an index named `customers`.
- `@Id`: The annotated field ensures that the document is unique within the index.
- `@Field`: Defines properties of the field (name, type, format, etc.)

- For further details on Object Mapping, you can refer to this [link](https://docs.spring.io/spring-data/elasticsearch/reference/#elasticsearch.mapping):


## Data Manipulation

Spring Data Elasticsearch offers two approaches for accessing and manipulating data: [Elasticsearch Repositories](https://docs.spring.io/spring-data/elasticsearch/reference/#elasticsearch.repositories) and [Elasticsearch Operations](https://docs.spring.io/spring-data/elasticsearch/reference/#elasticsearch.repositories). In our application, we will focus on utilizing the Spring Data Repository approach. However, for the purpose of this article, we will explore each method separately to gain insights into how Elasticsearch handles them.


### Elasticsearch Repositories

By utilizing the Repositories approach, Elasticsearch queries are constructed based on method names. Let’s first create our repository interface by extending `ElasticsearchRepository`.

```java
import io.github.jlmc.xsgoa.domain.entities.Customer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends ElasticsearchRepository<Customer, String> {
}
```

The current interface repository inherits various methods from `ElasticsearchRepository`, including `save()`, `saveAll()`, `findAll()`, etc. These inherited methods can be readily utilized in our application.

Implementing 3 additional methods to meet our search requirements.

```java
import io.github.jlmc.xsgoa.domain.entities.Customer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends ElasticsearchRepository<Customer, String> {

    List<Customer> findByNumber(Long number);

    List<Customer> findCustomerByNumberBetweenOrderByNumber(Long low, Long high);
}
```


As mentioned earlier, Elasticsearch queries are generated based on method names. For instance, a method named `findCustomerByNumberBetweenOrderByNumber` will be translated into the following Elasticsearch JSON query.

```
{
  "query": {
    "bool": {
      "must": [
        { "range": { "number": { "from": ?, "to": ?,  "include_lower": true, "include_upper": true } } }
      ]
    }
  }
}
```


### Elasticsearch Operations

Elasticsearch Operations provides a broad set of Operations Interfaces for interacting with Elasticsearch, including CRUD operations, index management, etc.

- `IndexOperations`: Defines Index-level actions such as creation and deletion.
- `DocumentOperations`: Defines entity-specific actions for storing, updating, and retrieving entities based on their id.
- `SearchOperations`: Defines entity-level actions including searching for multiple entities using queries.
- `ElasticsearchOperations`: Combines the DocumentOperations and SearchOperations interfaces.
To further understand their usage, let’s examine the following example with ElasticsearchOperations


To further understand their usage, let’s examine the following example with ElasticsearchOperations.

```java
@Data
@Document(indexName = "itemindex")
public class Item {
    @Id
    private int id;
    @Field(type = FieldType.Text, name = "name")
    private String name;
    @Field(type = FieldType.Double, name = "price")
    private Double price;
    @Field(type = FieldType.Keyword, name = "brand")
    private String brand;
    @Field(type = FieldType.Keyword, name = "category")
    private String category;
}
```


```java
@Service
@RequiredArgsConstructor
public class ItemService {
    // Injecting ElasticsearchOperations Bean
    private final ElasticsearchOperations elasticsearchOperations;
    /**
     * Persist the individual item entity in the Elasticsearch cluster
     */
    public int saveIndex(Item item) {
        Item itemEntity = elasticsearchOperations.save(item);
        return itemEntity.getId();
    }

    /**
     * Bulk-save the items in the Elasticsearch cluster
     */
    public List<Integer> saveIndexBulk(List<Item> itemList) {
        List<Integer> itemIds = new ArrayList<>();
        elasticsearchOperations.save(itemList).forEach(item -> itemIds.add(item.getId()));
        return itemIds;
    }

    /**
     * Remove a single item from the Elasticsearch cluster
     */
    public String findByCategory(Item item) {
        return elasticsearchOperations.delete(item);
    }
}
```

For more detailed information, please refer to the official Elasticsearch Operations documentation here: [Elasticsearch Operations](https://docs.spring.io/spring-data/elasticsearch/reference/#elasticsearch.operations)

Furthermore, Elasticsearch Operations has Query Interface which offers powerful capabilities for constructing and executing various types of search queries, applying filters, sorting, pagination, and aggregations. It allows you to build complex search logic using Elasticsearch’s query DSL (Domain-Specific Language) or JSON-based queries.

### CriteriaQuery

CriteriaQuery allows creating search queries for data without knowing the syntax or basics of Elasticsearch queries. Users can construct the queries by chaining and combining Criteria objects, where each object specifies specific criteria for searching documents.

Let’s examine the following example.

```java
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ElasticsearchOperations elasticsearchOperations;

    public SearchHits<Item> search(String name) {
        // Get all item with given name
        Criteria criteria = new Criteria("name").is(name);
        Query searchQuery = new CriteriaQuery(criteria);
        return elasticsearchOperations.search(searchQuery, Item.class);
    }
}
```

For more detailed information, please refer here: [CriteriaQuery](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/template.html#elasticsearch.operations.criteriaquery)

### StringQuery

This feature allows to construct Elasticsearch queries as JSON String. 
With StringQuery you can write Elasticsearch queries using the Elasticsearch query DSL (Domain-Specific Language) syntax directly in the form of a string. It provides flexibility when you need to construct complex queries dynamically or when you have existing queries in string format.

Here’s an example of how to use StringQuery.

```java
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ElasticsearchOperations elasticsearchOperations;

    public SearchHits<Item> search(String name) {
        // Get all item with given name
        Query query = new StringQuery(
                "{ \"match\": { \"name\": { \"query\": \"" + name + " \" } } } ");
        return elasticsearchOperations.search(query, Item.class);
    }
}
```
For more detailed information, please refer here: [StringQuery](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/template.html#elasticsearch.operations.stringquery)

### NativeQuery

This feature allows you to execute native queries against Elasticsearch. This one provides the maximum flexibility for constructing queries.

Here’s an example of how to use NativeQuery.

```java
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ElasticsearchOperations elasticsearchOperations;

    public SearchHits<Item> search(String name) {
        // Get all item with given name
        Query query = NativeQuery.builder()
                .withQuery(q -> q.match(m -> m.field("name").query(name))).build();
        return elasticsearchOperations.search(query, Item.class);
    }
}

```

For more detailed information, please refer here: [NativeQuery](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/template.html#elasticsearch.operations.nativequery)





---

## kibana:

1. Open the web browser in the http://localhost:5601, then navigate to manager > devtools
- http://localhost:5601/app/dev_tools#/console


## elastic

- How to log queries

```
PUT /_settings
{
  "index.search.slowlog.threshold.query.info": "1ms"
}

PUT _settings
{
  "index.search.slowlog.threshold.query.trace": "0s"
}
```

## Api endpoints

```shell
curl -XGET localhost:8080/api/customers | jq . > example-data.json
```

```shell
curl -XGET "localhost:8080/api/customers/_search?clientNumber=2&accountNumber=5" | jq . 
```

---

## Applications links

#### open api

- http://localhost:8080/v3/api-docs

#### swagger

- http://localhost:8080/swagger-ui/index.html

#### rabbitmq

- http://localhost:15672
  - credentials: guest/guest

#### kibana:

- http://localhost:5601
  1. Open the web browser in the http://localhost:5601, then navigate to manager > devtools
  2. http://localhost:5601/app/dev_tools#/console