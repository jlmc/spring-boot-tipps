# Spring boot tips 

## Description
Spring Boot Tips, Tricks and Techniques.
The goal of this repository is to place some examples of how to use and resolve some use cases under the spring boot technologies.


# Content

1. [Spring MVC](web)
2. [Spring Web Flux](spring-boot-tipps-reactive)
3. [AOP (Aspect Oriented Programming)](aop)
   1. What is AOP
   2. What is advice, join-point or pointcut?
   3. Types of AOP Advices
4. [Bean validations](bean-validation)
   1. validation handler with mvc
   2. validation with web flux
   3. exception advices starter example
5. [Qualifiers](qualifiers)
6. [kafka]()
   1. [Multiple kafka brokers in mvc project](kafka/spring-boot-kafka-example)
   2. [Multiple kafka brokers in web flux project](kafka/spring-boot-kafka-example-rx)
   3. [Traditional Messaging system vs kafka Stream platform](https://github.com/jlmc/spring-boot-tipps/commit/213ae6a6ecedf1e92113206e5c54a77aa2195c8f)
   4. [Kafka topics and partitions](https://github.com/jlmc/spring-boot-tipps/commit/bd803cdfe9cd16d56a294e7533685ca880473dc9)
   5. [Kafka docker-compose.yml configurations](https://github.com/jlmc/spring-boot-tipps/commit/8898fc4767e24542e8d277b777c0326cd350b25c)
   6. [Sending message with key](https://github.com/jlmc/spring-boot-tipps/commit/78b955591c73176dc448866698a38a8cccf57dff) 
   7. [Kafka consumer group id](https://github.com/jlmc/spring-boot-tipps/commit/fe16f725c71fa15b61ef799956ea73d5242bd909)
   8. [kafka Retention policy](https://github.com/jlmc/spring-boot-tipps/commit/6aae7edb7e1d99f1a2c6524af18728e3f4af3128)
   9. [Introduction to Spring KafkaTemplate to Produce Messages](https://github.com/jlmc/spring-boot-tipps/commit/2e77a1a3140e8c8d2fb70cda4aafd5c1f4d7cd43)
   10. [How to create topic programmatically](https://github.com/jlmc/spring-boot-tipps/commit/e7a963cf4c8f46159a1456afa95cb6c143f30f3d)
   11. [Send message to default topic](https://github.com/jlmc/spring-boot-tipps/commit/d77487ab46e591ed4682bdb6efcd67e5303cbfee)
   12. [Send message to default topic with headers](https://github.com/jlmc/spring-boot-tipps/commit/543aedc473dd643a2a260ebda76ee74b8716adc4)
   13. [Configure Integration tests in an IT dedicated](https://github.com/jlmc/spring-boot-tipps/commit/5ee356c4292a13573c0cb9779589c5592a1a52b2)
   14. [kafka integration tests with Embedded Kafka](https://github.com/jlmc/spring-boot-tipps/commit/4c49427b93da5af4fe95ea0a2973d8a635fea8f6)
   15. [Kafka Producer Important Configurations](https://github.com/jlmc/spring-boot-tipps/commit/c04d4bbb27ddf7ad7678a4cd480c344b81e03f4a)
   16. [Configure Kafka Consumer using SpringBoot Profiles - application.yml](https://github.com/jlmc/spring-boot-tipps/commit/711b1e132061cafea3d9b0e91d0a324dcf521a3b)
   17. [How to commit offsets manually](https://github.com/jlmc/spring-boot-tipps/commit/da3efc9ca170d58d1a3156c6512c0dd68b83baba)
   18. [How to create Kafka Concurrent Consumers Message Listeners](https://github.com/jlmc/spring-boot-tipps/commit/4c9062e542c9906b0d5bb62daae9413865db3171)
   19. [Using only one consumer thread](https://github.com/jlmc/spring-boot-tipps/commit/f4d9704b8fb1fecfb1204cb009a3704c95d050d4)
   20. [Integration with spring data (jpa)](https://github.com/jlmc/spring-boot-tipps/commit/21036a19eaec8df11e3520dac2a1286f5c797fcc)
   21. [Configure Embedded Kafka for Integration Tests](https://github.com/jlmc/spring-boot-tipps/commit/fb4992b4a11d6150040c3974abffada776c1ec41)
   22. [Integration test for kafka consumer Exception throws](https://github.com/jlmc/spring-boot-tipps/commit/895b5a49a12055fca5fc5686a44bc0d7a127367c)
   23. [Custom Error Handler and Custom Retry in Kafka Consumer](https://github.com/jlmc/spring-boot-tipps/commit/e7176c4ce79657938255621237e7ae3c6392ec4c)
   24. [Add a RetryListener to monitor each Retry attempt](https://github.com/jlmc/spring-boot-tipps/commit/77d2634740f525afb18c31abb5a17056715d8141)
   25. [kafka consumer, Retry SpecificExceptions using Custom RetryPolicy](https://github.com/jlmc/spring-boot-tipps/commit/d3e22678770167e8357ec6d3454512af146c8987)
   26. [kafka consumer, Retry failed Records with ExponentialBackOff](https://github.com/jlmc/spring-boot-tipps/commit/e857e1624fb24dfbb96f2c35d7e645b942e63c22)
   27. [Kafka consumers recovery types](https://github.com/jlmc/spring-boot-tipps/commit/24452e16a55fd86bbbac3fc57b105fd6e0acf4c6)
   28. [Recovery : Publish the message to the Retry Topic](https://github.com/jlmc/spring-boot-tipps/commit/b8c64b52f8a4b30bbe0a459a173ce88644a0af50)
   29. [Recovery: Refactor the EventsConsumer Integration test](https://github.com/jlmc/spring-boot-tipps/commit/ec245536be809e4a60da5249bdee9b5edfa21602)
7. [Jackson](jackson)
   1. Serialization demos
   2. jackson tests with @JsonTest annotation
8. Spring security
9. [Processing csv file with open csv](upload-processing/upload-csv/README.md)
   1. Mongo db migration with mongock in web flux project
10. [Spring reactive with acid database (R2DBC)](spring-boot-tipps-reactive/spring-boot-r2dbc-acid-rx)

