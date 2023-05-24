# About

React Application:  https://github.com/Vikas-Kumar56/firststep-graphql-client

Spring boot Application:  https://github.com/Vikas-Kumar56/firststep-graphql-kotlin


## hello world

1. http://localhost:8080/graphiql?path=/graphql

```graphql
query {
  helloworld
}
```

```
{
  "data": {
    "helloworld": "Hello World!"
  }
}
```

```shell
 curl 'http://localhost:8080/graphql' \
  -H 'Referer: http://localhost:8080/graphiql?path=/graphql' \
  -H 'accept: application/json, multipart/mixed' \
  -H 'content-type: application/json' \
  --data-raw '{"query":"query {  helloworld }"}' | jq .
```
