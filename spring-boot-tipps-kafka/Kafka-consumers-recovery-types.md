## Kafka consumers recovery types

- **Approach 1**: Reprocess the failed the record again
    - Example: Service the consumer interacts with a temporary down service.
        - let's say the consumer invoke another microservice service via http, but that service not available for a period of time. we may have to replay the message so that is succeeded.

- **Approach 2**: Discard the message and ove on 
  - invalid message: passing errors, invalid events
  - For example, in our example send an event with invalid product id is an invalid event, and there is no point in recording the same message again and gain as part of the recovery logic.

### Approach 1

Option 1 : Publish the failed message to Retry topic

- and have a consumer reading from the retry topic that invokes the consumer logic again.


Option 2 : Save the failed message in a DB and retry with a scheduler.
