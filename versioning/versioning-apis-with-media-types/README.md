# How to consume multiple Version using Custom media type

All the Request must contain the Accept Header witch will be used to select the right implementation version of consumer that should consume the Request.

### How to consume the version 1

```shell script
curl --location --request GET 'http://localhost:8081/books' \
--header 'Accept: application/vnd.example.v1+json'
```

### How to consume the version 2

```shell script
curl --location --request GET 'http://localhost:8081/books' \
--header 'Accept: application/vnd.example.v2+json'
```