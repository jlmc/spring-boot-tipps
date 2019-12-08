# Notes



### 2.24. Overriding properties via command line and environment variables

To run the application we can just execute the command:

```sh
java -jar target/my-jar-file.jar
``` 

But if we want to replace application variables externally we can do so in two ways.

1. Using command line parameters:

```sh
java -jar target/my-jar-file.jar \
    --server.port=8082 \
    --app.maxasyncthreads=10
```

2. Using environment variables:

But here we should have some extra attention. By convention everything must be in UPPER CASE and in place of the character '.' the '_' character must be use:

```sh
export SERVER_PORT=8082
export APP_MAXASYNCTHREADS=10

java -jar target/my-jar-file.jar
```


# 2.28. Enabling Spring Profile by Command Line and Environment Variable


1. Using command line parameters:

```sh
java -jar target/my-jar-file.jar \
	--spring.profiles.active=prod,any-pthers

```

2. Using environment variables:

```sh
export SPRING_PROFILES_ACTIVE=prod,any-pthers
java -jar target/my-jar-file.jar
```