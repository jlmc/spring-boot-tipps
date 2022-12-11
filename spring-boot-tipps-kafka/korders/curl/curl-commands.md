# GET ORDER BY ID
---------------------
```shell
curl -v -L -X GET localhost:8080/v1/orders/1234
```

# CREATE NEW ORDER

```shell
 curl -v -L -X POST 'localhost:8080/v1/orders' \
-H 'Content-Type: application/json' \
--data-raw '{
    "items": [ {
      "productId" : "mac-pro-1",
      "qty" : 1
     }, 
     {
      "productId" : "iphone-1",
      "qty" : 2
     } 
    ]
}' \
 | jq .
```

```shell
curl -v -L -X POST localhost:8080/v1/orders \
 -H "Content-Type: application/json" \
 -d @../src/test/resources/request-payloads/create-order-request-payload.json \
  | jq .
```
