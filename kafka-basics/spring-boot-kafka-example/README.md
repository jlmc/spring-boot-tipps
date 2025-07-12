# Order api


```shell
curl -v -L -X POST 'localhost:8080/orders' \
-H 'Content-Type: application/json' \
--data-raw '{
    "address": "cesamos",
    "item": "12"
}' \
 | jq .
```


## kafka terminal commands

```
docker exec -it kafka1 /bin/bash

kafka-topics --zookeeper zoo1:2181  --list
kafka-topics --zookeeper zoo1:2181 --delete --topic topic1


kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic topic1 --from-beginning --property print.key=true --property key.separator=

kafka-console-consumer --zookeeper zoo1:2181 --topic topic1 --from-beginning --property print.key=true --property key.separator=,
```



kafka-topics --zookeeper platform-zoo:2181  --list

kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic topic1 --from-beginning --property print.key=true --property key.separator=
