# Authentication Server implementation


### Password Credentials Grant Flow

- Notes: Authorization header is the base authentication with `app-client-id` and `app-client-secret`, In this case: `food4u-web:web123` encoded to `Base64`

Request:
```
POST /oauth/token HTTP/1.1
Host: localhost:8081
Content-Type: application/x-www-form-urlencoded
Authorization: Basic Zm9vZDR1LXdlYjp3ZWIxMjM=

grant_type=password&password=pwd&username=john
``` 

    

Response:

```
Status: 200
{
    "access_token": "617185a5-7d59-43ef-9bfe-9bb4a13c3e07",
    "token_type": "bearer",
    "expires_in": 21599,
    "scope": "write read"
}
```



CURL command
```
curl --location --request POST 'http://localhost:8081/oauth/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Authorization: Basic Zm9vZDR1LXdlYjp3ZWIxMjM=' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'password=pwd' \
--data-urlencode 'username=john'
```
