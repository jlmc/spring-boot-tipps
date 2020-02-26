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


# OAuth 2.0 Token Introspection

- Token Introspection is the process that in the Resource-Server verify that a given access-token is valid in Authentication-Server, before process the client request.

- A especificação OAuth 2.0 não especifica como este pedido deve ser constituido. No entanto existe uma outra especificação [OAuth 2.0 Token Introspection - https://tools.ietf.org/html/rfc7662](https://tools.ietf.org/html/rfc7662) a qual especifica de como este processo deve ser feito.

- O `Spring Security OAuth`, não segue a especificação, porque a especificação é mais recente que o proprio project. Alem disso a Pivotal tem inteções de depreciar o projecto `Spring Security OAuth` em um futuro muito proximo.


Request:
```
POST /oauth/check_token HTTP/1.1
Host: localhost:8081
Content-Type: application/x-www-form-urlencoded
Authorization: Basic Zm9vZDR1LXdlYjp3ZWIxMjM=

token=5d0570ea-a16f-4e37-ad50-c1c601e120f6
```

Response:
```
Status: 200
{
    "active": true,
    "exp": 1582496350,
    "user_name": "john",
    "authorities": [
        "ROLE_ADMIN",
        "ROLE_CLIENT"
    ],
    "client_id": "food4u-web",
    "scope": [
        "write",
        "read"
    ]
}
```

# OAuth 2.0 Refresh token


1. get Access token
    ```
    POST /oauth/token HTTP/1.1
    Host: localhost:8081
    Content-Type: application/x-www-form-urlencoded
    Authorization: Basic Zm9vZDR1LXdlYjp3ZWIxMjM=
    
    grant_type=password&password=pwd&username=john
    ```
    
    ```
    {
        "access_token": "68edf25d-6d46-4413-8a3a-981009665fca",
        "token_type": "bearer",
        "refresh_token": "8bde02a7-0394-4460-a7fb-f45e0ba010ad",
        "expires_in": 21599,
        "scope": "write read"
    }
    ```
2. Refresh token
    ```
    POST /oauth/token HTTP/1.1
    Host: localhost:8081
    Content-Type: application/x-www-form-urlencoded
    Authorization: Basic Zm9vZDR1LXdlYjp3ZWIxMjM=
    
    refresh_token=8bde02a7-0394-4460-a7fb-f45e0ba010ad&grant_type=refresh_token
    ```
   
   ```
   {
       "access_token": "a8f66b5a-9a76-4f50-baf0-a79d9c8bc779",
       "token_type": "bearer",
       "refresh_token": "8bde02a7-0394-4460-a7fb-f45e0ba010ad",
       "expires_in": 21599,
       "scope": "write read"
   }
   ```
   
   