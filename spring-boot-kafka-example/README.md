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
