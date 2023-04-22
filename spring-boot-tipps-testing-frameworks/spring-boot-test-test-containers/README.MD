# COMMAND


```shell
curl -L -X GET 'localhost:8080/todos'
```

```shell
curl -L -X POST 'localhost:8080/todos' \
-H 'Content-Type: application/json' \
--data-raw '{
        
        "title": "other",
        "description": "Test container leaning in spring boot!!!"
    }'
```
