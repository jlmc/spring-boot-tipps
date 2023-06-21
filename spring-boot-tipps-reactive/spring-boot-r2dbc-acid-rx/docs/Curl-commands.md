# Create user

```sh
curl -v -L -X POST localhost:8080/users \
-H "Content-Type: application/json" \
-d @payloads/create-user-request-payload.json \
| jq .
```

```sh
curl -v -L -X POST localhost:8080/users \
-H "Content-Type: application/json" \
-d @payloads/create-user-request-payload-blank.json \
| jq .
```

# get users

```sh
curl -v -L -X GET localhost:8080/users \
-H "Content-Type: application/json" \
| jq .
```

# get user by id

```sh

export USER_ID="00000000-0000-0000-0000-000000000001"

curl -v -L -X GET localhost:8080/users/$USER_ID \
-H "Content-Type: application/json" \
| jq .
```

# get user posts

```sh
export USER_ID="00000000-0000-0000-0000-000000000001"

curl -v -L -X GET localhost:8080/users/$USER_ID/posts \
-H "Content-Type: application/json" \
| jq .
```

```sh
export USER_ID="00000000-0000-0000-0000-000000000099"

curl -v -L -X GET localhost:8080/users/$USER_ID/posts \
-H "Content-Type: application/json" \
| jq .
```

# Add user post

```sh
export USER_ID="00000000-0000-0000-0000-000000000001"

curl -v -L -X POST localhost:8080/users/$USER_ID/posts \
-H "Content-Type: application/json" \
-d @payloads/create-user-post-request-payload.json \
| jq .
```

create-user-post-request-payload.json
