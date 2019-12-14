# Requests

### GET /cookers
```shell script
curl -X GET -v \
    -H "Accept:application/xml" \
    -H "Content-Type:application/xml" \
    http://localhost:8080/cookers 
```

### GET /cookers/{id}
```shell script
curl -X GET -v \
    -H "Accept:application/xml" \
    -H "Content-Type:application/xml" \
    http://localhost:8080/cookers/1
```

### POST /cookers
```shell script
curl -X POST -v \
  http://localhost:8080/cookers \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -H 'Host: localhost:8080' \
  -d '{"id":null,"title":"ddd-1"}'
```


### POST /restaurants
```shell script
curl -X POST \
  http://localhost:8080/restaurants \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -H 'Host: localhost:8080' \
  -H 'User-Agent: curl' \
  -d '{
        "name": "River house",
        "takeAwayTax": 0.50,
        "cooker": {
            "id": 1,
            "title": "Braz Jorge"
        }
}'
```

### PUT /restaurants
```shell script
curl -X PUT \
  http://localhost:8080/restaurants/1 \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -H 'Host: localhost:8080' \
  -H 'cache-control: no-cache' \
  -d '{
    "name": "Dona Maria 2",
    "takeAwayTax": 0.30,
    "cooker": {
        "id": 2,
        "title": "CCC"
    }
}'
```

### PATCH /restaurants
```shell script
curl -X PATCH \
  http://localhost:8080/restaurants/1 \
  -H 'Accept: */*' \
  -H 'Content-Length: 60' \
  -H 'Content-Type: application/json' \
  -H 'Host: localhost:8080' \
  -d '{
    "name": "Dona Maria 3",
    "takeAwayTax": 0.50
}'
```
