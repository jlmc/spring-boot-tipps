# I Do It

create the docker database 


Application db

```
docker run --name idoitdb \
    -p 8432:5432 \
    -e POSTGRES_USER=idoitdb \
    -e POSTGRES_PASSWORD=idoitdb \
    -e POSTGRES_DB=idoitdb -v "/Users/costa/Documents/junk/volumes/docker/idoitdb:/var/lib/postgresql/data" \
    -d postgres:11.1
```

Application tests db
```
docker run --name idoit2db \
    -p 9432:5432 \
    -e POSTGRES_USER=idoit2db \
    -e POSTGRES_PASSWORD=idoit2db \
    -e POSTGRES_DB=idoitdb -v "/Users/costa/Documents/junk/volumes/docker/idoit2db:/var/lib/postgresql/data" \
    -d postgres:11.1
```
