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

##  Parameterized GraphQL Query

```graphql
type Query {
    greet(name: String!): String!
}
```

- The '!' means that the parameter or return value will never be null
    - This means that if we want to declare a parameter as required or mandatory we can mark the parameter type with the `!` operator. 

```graphql
type Query {
    helloworld: ID
}
```

- If we need to parametrize an identifier (what every is each type) the keyword that should be used is the `ID` 

### calling the methods with arguments

```
http://localhost:8080/graphiql?path=/graphql
```

```
query {
  greet(name: "John")
}
```

```
curl 'http://localhost:8080/graphql' \
  -H 'Accept-Language: en-GB,en' \
  -H 'Origin: http://localhost:8080' \
  -H 'Referer: http://localhost:8080/graphiql?path=/graphql' \
  -H 'accept: application/json, multipart/mixed' \
  -H 'content-type: application/json' \
  --data-raw '{"query":"query {\n  greet(name: \"John\")\n}\n\n"}'
```
