# kafka


## Event Stream platform

1. An *Even streaming platform allows* the application to produce and consume a stream of records, as like a messaging service, 
  - You can think of this as a `Pub Sub Model`.
    - The producer and consumer here are independent of each other, meaning the producer has no clue about which consumer is going to read this message.

2. An even streaming platform also *stores* the stream of events so that it can be replayed if it's necessary, events are generally retained in multiple servers for people waiting for **tolerance** and **availability**.

3. An Even streaming platform also allows the application to process the records as they occur.

👆Basically, these three principles form the foundation for the event streaming platform, Apache Kafka, build on top of these principles.


### Traditional Messaging system vs kafka

#### Traditional Messaging System

- Transient Message Persistence
  - after the message has read by the consumers, the message will be removed from the message broker. 
- Brokers are responsibility to keep tack of consumed messages.
  - and remove them from the broker when the message are read.
- Target a specific consumer, to read a message from the broker.
- Not a distributed system


#### kafka Stream platform

- Store events based on a retention time. events are Immutable, once the records are sent to kafka, then it cannot be altered.
- It is responsibility of the consumer to keep track of the consumed messages
- Any Consumer can access a message from the broker.
- It is a distributed streaming system. 
  - a distributed system in general is proven to handle the load very well and has the intelligence to distribute the Load.



