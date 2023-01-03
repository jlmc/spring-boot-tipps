# Kafka Producer Error Handling

The Kafka-Producer produces the messages to the Kafka topic. But what are the possible errors that can happen in this process?

- If the kafka cluster is not available, it's going to throw some errors.

- if `acks=all`, and some brokers are not available in the cluster, then the producer will throw some errors back into the application.

- `min.insync.replicas` config, This is one of the important configurations when it comes to reliable data delivery.
    - It can be set at the local level or the topic level.
    - Example: `min.insync.replicas=2` but only one broker is available
        - The value 2 means that we need to have 2 replicas of the data that is produced into the topic in our local.
        - So, if we have 3 brokers but only one is available on the message producer moment, then the producer will throw an error, saying that there are not enough replicas


---

## Broker Not Available

- To test this problem scenario,
  1. startup Kafka cluster
  2. startup the application korders
  3. shutdown the Kafka cluster
  4. Send a curl command, to create a new order event

- we will see that:
  - by default, during 60 seconds the kafka producer (`org.apache.kafka.client.NetworkClient`) will try to send the message. 
  - after 60 seconds is throw a Timeout Exception (`org.apache.kafka.common.errors.TimeoutException`): `Topic orders-events not present in metadata after 60000ms` 
  - And continue to try to send the message infinitely, until push the message successful.

- The reason for this error metadata description, is that behind the scenes when we send a message, the very first call is to invoke the Kafka cluster to get the metadata to get the last information about the Kafka cluster.

## Retry in kafka producer - `min.insync.replicas`

- let's say that `min.insync.replicas=2` but only one broker is available
- The value 2 means that we need to have 2 replicas of the data that is produced into the topic in our local.
- So, if we have 3 brokers but only one is available on the message producer moment, then the producer will throw an error, saying that there are not enough replicas

👇The retries configuration is very important in the scenario, it means how many times the producer will try to send the message to the cluster replicas before marking the message as fail.
The `retry.backoff.ms` define the interval in millis seconds between the retries
```
  kafka:
    producer:
      properties:
        acks: all
        retries: 10
        retry.backoff.ms: 1000
```

## Retain/Recover Failed Records in kafka Producer

- One thing we can do is to use the `OutBox Letter` pattern 1. 
  - when the message sends failed, persist the record into the database.
  - have a scheduler retrying to publish the message in the database.
