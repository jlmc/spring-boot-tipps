# Hibernate 2LC with Redis

## References
- https://medium.com/@shahto/scaling-spring-boot-with-hibernate-2nd-level-cache-on-redis-54d588fc8b06
- https://medium.com/@himani.prasad016/caching-in-hibernate-3ad4f479fcc0
- https://github.com/redisson/redisson
- https://dzone.com/articles/caching-in-hibernate-with-redis
- https://dzone.com/articles/hibernate-redis-and-l2-cache-performance

## How to

### Choosing an L2 cache provider

The scope of Second-level Caching in Hibernate is application-level, which means that the cached data is available across multiple sessions.

Hibernate supports three cache modes for Second-level Caching:
- read-only
    - The read-only cache mode is used for data that is not expected to change frequently
- read-write
    - The read-write cache mode is used for data that is frequently updated.
- transactional.
    - The transactional cache mode is used for data that is updated within a transaction.

The cache concurrency strategy determines how multiple threads access the cache.
Hibernate supports multiple cache concurrency strategies such as `READ_ONLY`, `NONSTRICT_READ_WRITE`, `READ_WRITE`, and `TRANSACTIONAL`.

- `READ_ONLY`:
    - Objects do not change once inside the cache.
    - Used only for entities that will not change once inside the cache.
- `NONSTRICT_READ_WRITE`:
    - Objects are (eventually) modified if their corresponding database entry is modified.
    - The cache is updated after a transaction modifies the entity in the database. Not able to guarantee strong consistency but can guarantee eventual consistency.
- `READ_WRITE`:
    - Objects are immediately modified if their corresponding database entry is modified, ensuring strong consistency.
    - Guarantees strong consistency by using "soft" locks that maintain control of the entity until the transaction is complete.
- `TRANSACTIONAL`: Objects are modified using distributed XA transactions, guaranteeing data integrity.

To ensure data consistency, Hibernate provides a cache synchronization mechanism that automatically updates the cache when the database is updated.
This mechanism is called cache invalidation.
When an entity is updated, Hibernate invalidates the corresponding cache entry, and the next time the entity is accessed,
it is fetched from the database and stored in the cache.

#### Query Cache
Query caching is a caching mechanism provided by Hibernate to cache the results of queries.
When a query is executed, Hibernate checks if the query results are already cached.
If the results are found in the cache, they are returned without hitting the database, otherwise, the query is executed and the results are cached for future use.

To enable Second-level Caching in Hibernate, you need to configure a cache provider in your Hibernate configuration file.  
Hibernate supports multiple cache providers such as Ehcache, Infinispan, and Hazelcast.
You also need to annotate the entities that you want to cache with the `@Cacheable` annotation.

Hibernate L2 cache supports several providers. Two popular choices are Ehcache and Redis.

This table highlight the major differences that you should consider when choosing a provider:

|              | Ehcache                           | Redis                                       |
|--------------|-----------------------------------|---------------------------------------------|
| Performance  | ✅ Quick response times           | Slower due to cross-server communications   |
| Scalability  | Standalone cache                  | ✅ Distributed cache that can be clustered  |
| Durability   | Requires paid extension           | ✅ Supports several durability strategies   |
| Ease of use  | ✅ Simple to set up and configur  | Lots of configurations                      |
| Integrations | JVM languages only                | ✅ Wide range of ubiquitous data types      |


In this article we’re going to use Redis in a single server configuration.

You could choose to install Redis natively on your development machine (Redis is officially supported on Linux and Mac, but not Windows).
Alternatively, you could run a dockerized Redis image:

```shell
docker-compose up -d
```

```shell
docker run -name <container-name> -d redis

docker exec -it <container-name> redis-cli

$ ping
```

### Configuring Redisson


1. we need to add and configure Redisson (Redis Java client).

    ```xml
    <!-- Hibernate 2l cache redis provider -->
    <dependency>
        <groupId>org.redisson</groupId>
        <artifactId>redisson-hibernate-6</artifactId>
        <version>3.27.1</version>
    </dependency>
    ```

2. Then add the following basic Redisson configuration file redisson.yaml to your CLASSPATH under resources. Make sure the “address” property matches your setup.

   for a single redis server
    ```yaml
    singleServerConfig:
      idleConnectionTimeout: 10000
      connectTimeout: 10000
      timeout: 3000
      retryAttempts: 3
      retryInterval: 1500
      password: redis
      subscriptionsPerConnection: 5
      clientName: null
      address: "redis://localhost:6379"
      subscriptionConnectionMinimumIdleSize: 1
      subscriptionConnectionPoolSize: 50
      connectionMinimumIdleSize: 10
      connectionPoolSize: 64
      database: 0
      dnsMonitoringInterval: 5000
    ```

   or for a cluster using sentinel

    ```yaml
    sentinelServersConfig:
      idleConnectionTimeout: 10000
      connectTimeout: 10000
      timeout: 3000
      retryAttempts: 3
      retryInterval: 1500
      failedSlaveReconnectionInterval: 3000
      failedSlaveCheckInterval: 60000
      password: redis
      subscriptionsPerConnection: 5
      clientName: null
      #loadBalancer: !<org.redisson.connection.balancer.RoundRobinLoadBalancer> {}
      subscriptionConnectionMinimumIdleSize: 1
      subscriptionConnectionPoolSize: 50
      slaveConnectionMinimumIdleSize: 24
      slaveConnectionPoolSize: 64
      masterConnectionMinimumIdleSize: 24
      masterConnectionPoolSize: 64
      readMode: "SLAVE"
      subscriptionMode: "SLAVE"
      sentinelAddresses:
        - "redis://127.0.0.1:26379"
        #- "redis://127.0.0.1:26389"
      masterName: "mymaster"
      database: 0
      checkSentinelsList: false
    #threads: 16
    #nettyThreads: 32
    #codec: !<org.redisson.codec.Kryo5Codec> {}
    #transportMode: "NIO"
    ```

3. Enabling L2 cache in the code.

   To enable caching for an entity, annotate it with the `@Cacheable` annotation. You can also specify the cache concurrency strategy using the `@Cache` annotation.

   Add the for example the annotation at the class top level in
    ```java
    @org.hibernate.annotations.Cache(region = "ClubEntityCache", usage = CacheConcurrencyStrategy.READ_WRITE)
    ```

   To learn more about cache concurrency strategies, refer to Hibernate’s documentation on this subject.

    - https://docs.jboss.org/hibernate/orm/6.4/introduction/html_single/Hibernate_Introduction.html#second-level-cache

4. The next (and final) step, is to enable caching in application.properties:

    ```yaml
    spring:
      jpa:
        properties:
          hibernate:
            cache:
              use_second_level_cache: true
              use_query_cache: true
              redisson.config: redisson-sentinel.yaml
            #         hibernate.cache.redisson.config:
    ```

5. Redis Hint

Hint 1: You could flush Redis at any time using the “FLUSHALL” command.

Hint 2: If you wish to monitor the handshakes on Redis for the next exercise, you can execute the command “MONITOR”.

