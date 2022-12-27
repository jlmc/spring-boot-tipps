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
      "productId" : "1",
      "qty" : 1
     }, 
     {
      "productId" : "2",
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

# CREATE NEW ORDER with UNKNOWN PRODUCT

```shell
 curl -v -L -X POST 'localhost:8080/v1/orders' \
-H 'Content-Type: application/json' \
--data-raw '{
    "items": [ {
      "productId" : "UNKNOWN",
      "qty" : 1
     } 
    ]
}' \
 | jq .
```

```shell
 curl -v -L -X POST 'localhost:8080/v1/orders' \
-H 'Content-Type: application/json' \
--data-raw '{
    "items": [ {
      "productId" : "9999_9999",
      "qty" : 1
     } 
    ]
}' \
 | jq .
```


# UPDATE AN EXISTING NEW ORDER

```shell
 curl -v -L -X PUT 'localhost:8080/v1/orders/5126e996' \
-H 'Content-Type: application/json' \
--data-raw '{
    "items": [ {
      "productId" : "1",
      "qty" : 1
     }, 
     {
      "productId" : "2",
      "qty" : 3
     } 
    ]
}' \
 | jq .
```

```shell
curl -v -L -X PUT localhost:8080/v1/orders/5126e996 \
 -H "Content-Type: application/json" \
 -d @../src/test/resources/request-payloads/create-order-request-payload.json \
  | jq .
```
