# curl commands


### GET ALL MOVIES
```shell
curl -v -L -X GET localhost:8080/movies \
-H "Content-Type: application/json" \
| jq .
```

### GET MOVIE BY ID
```shell
curl -v -L -X GET localhost:8080/movies/1 \
-H "Content-Type: application/json" \
| jq .
```

### CREATE A NEW BOOK
```sh
curl -v -L -X POST localhost:8080/movies \
-H "Content-Type: application/json" \
-d @payloads/create-movie-request-payload.json \
| jq .
```
