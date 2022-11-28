# CORS

Useful links: 

[Definition of simple requisition according to CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS#Simple_requests)

## Notes:

- Origin: protocol:host:port

---
## Simple requests HTTP Headers

A Simple requisitions have the following restrictions:


1. One of the allowed methods: 
  
    - GET 
    - HEAD 
    - POST


2. Some HTTP Header also define if the requisition is simple or not. If we add to the HTTP Request any Header that is not in the following list the Request **is no longer** a **simple request**:

    - Accept
    - Accept-Language
    - Content-Language
    - Content-Type (but note the additional requirements below)
    - DPR
    - Downlink
    - Save-Data
    - Viewport-Width
    - Width


3. The only allowed values for the Content-Type header are in the following list (even if it's a GET, HEAD, or POST request). For example if we use the header `Content-Type: application/json` then the requisition is not a simple request, no matter the wich Http verb we use. 

    - application/x-www-form-urlencoded
    - multipart/form-data
    - text/plain



### Simple Request flow:

1. the browser sent the A GET, HEAD or POST with the header:
```
Origin: http://example-client-js.com:8000
```

2. The server should retrieve a Response with the Header:
```
Access-Control-Allow-Origin: http://example-client-js.com:8000
```

About this Header we can say:

- ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
- Header required when you have cross-source clients.
- when is not present the clients suffer from CORS issue.
- When the API is public and cannot specify the value of this header then the value '*' should be used.
- When there is a possibility to know exactly the client `Origin`, then the request `Origin` value should be validated and returned:




## [The Preflight Request](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS#Preflighted_requests)

- If The Request is not a Simple Request then it may need a Preflighted requests before (`OPTIONS`)
    - Other wise, the requisition is refused, because in this case before the put request is executed a `OPTIONS` request.
    - This behavior is called `preflight` (Em Portugues, Pré voo, ou, Pré envio)
    - The Server when receive the preflight request it should return the CORS HTTP headers indicated if the the request is allowed, or, the status 403.

![The preflight request flow](https://mdn.mozillademos.org/files/16753/preflight_correct.png)

#### The preflight response can specify the max age of the preflight request using the HTTP Headers: 

- Access-Control-Allow-Origin
    - Specifies which domain can interpret the response

- Access-Control-Allow-Methods
    - HTTP verbs which are allowed (it does not say which of the verbs are not allowed, can return only one verb, even if several verbs are actually accepted)
    - Example: 'GET, POST, PUT, DELETE, HEAD, PATCH'
    - The request header is use: `Access-Control-Request-Method`

- Access-Control-Max-Age
    - Maximum time in seconds the preflight response is considered valid and can be cached.

- Access-Control-Allow-Headers

    - What the headers are allowed
    - The request Header `Access-Control-Request-Headers` is used

example:
```
Access-Control-Allow-Origin: http://foo.example
Access-Control-Allow-Methods: POST, GET, OPTIONS
Access-Control-Allow-Headers: X-PINGOTHER, Content-Type
Access-Control-Max-Age: 86400
```


### Requests with credentials

The most interesting capability exposed by both XMLHttpRequest or Fetch and CORS is the ability to make "credentialed" requests that are aware of HTTP cookies and HTTP Authentication information. By default, in cross-site XMLHttpRequest or Fetch invocations, browsers will not send credentials. A specific flag has to be set on the XMLHttpRequest object or the Request constructor when it is invoked.

In this example, content originally loaded from http://foo.example makes a simple GET request to a resource on http://bar.other which sets Cookies. Content on foo.example might contain JavaScript like this:

```javascript
const invocation = new XMLHttpRequest();
const url = 'http://bar.other/resources/credentialed-content/';
    
function callOtherDomain() {
  if (invocation) {
    invocation.open('GET', url, true);
    invocation.withCredentials = true;
    invocation.onreadystatechange = handler;
    invocation.send(); 
  }
}
```

Line 7 shows the flag on XMLHttpRequest that has to be set in order to make the invocation with Cookies, namely the `withCredentials` boolean value. 
By default, the invocation is made without Cookies. Since this is a simple GET request, it is not preflighted, but the browser will reject any response that does not have the `Access-Control-Allow-Credentials: true` header, and not make the response available to the invoking web content.


### Access-Control-Allow-Credentials
The Access-Control-Allow-Credentials header Indicates whether or not the response to the request can be exposed when the credentials flag is true. When used as part of a response to a preflight request, this indicates whether or not the actual request can be made using credentials.
 Note that simple GET requests are not preflighted, and so if a request is made for a resource with credentials, if this header is not returned with the resource, the response is ignored by the browser and not returned to web content.


---
## Spring support for CORS

### Manually

```
 return ResponseEntity.ok()
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .body(list);
```

### Annotation `CrossOrigin`

- The Spring previous the annotation `@org.springframework.web.bind.annotation.CrossOrigin`
- This annotations allow us to define the origin that should be validated.
- By default the `*` is used, therefore all  the origins are allowed.
- If the origins do not match with none of the expected ones, then the response will be 403. and the request is not processed by the server.
- Therefore, Therefore, this implementation does more than just add HTTP headers.
- This only works because by default the browsers already send in the HTTP Request the Header `Origin`

```
@org.springframework.web.bind.annotation.CrossOrigin(maxAge = 20)
```


### Configure cords globally for all the project:

```java
package io.costax.food4u.core.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The web configuration class enables the CORS in all the request
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        // any request for the path mapping
        registry.addMapping("/**")
            //.allowedOrigins("http://foo.com")
            .allowedOrigins("*")
            //.allowedMethods("GET", "POST", "HEAD")
            .allowedMethods("*")
            .maxAge(1800)
            .allowCredentials(true)
        ;
    }
}
```
