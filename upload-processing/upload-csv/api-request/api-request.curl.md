
```sh
curl -v -X GET http://localhost:8080/heroes -H"Content-Type: application/json" | jq .
```

```sh
curl -X POST http://localhost:8080/heroes \
  -H"Content-Type: application/json" \
  -d'
    {
    "id": null,
    "name": "Batman",
    "nickName": "bat"
  }
  ' | jq .
```

```sh
curl -v -X POST http://localhost:8080/heroes \
  -H"Content-Type: application/json" \
  -d @create-batman-payload.json | jq .
```
