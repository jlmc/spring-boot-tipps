# 

```
curl -X GET -v \
    -H "Accept:application/xml" \
    -H "Content-Type:application/xml" \
    http://localhost:8080/cookers 
```


```
curl -X GET -v \
    -H "Accept:application/xml" \
    -H "Content-Type:application/xml" \
    http://localhost:8080/cookers/1
```

```
curl -X POST -v \
  http://localhost:8080/cookers \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -H 'Host: localhost:8080' \
  -d '{"id":null,"title":"ddd-1"}'
```