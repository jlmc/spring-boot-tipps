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

## Return Array From GraphQL Query

```kotlin
    @QueryMapping
    fun gerRandomNumbers(): List<Int> {
        return listOf(1, 2, 3)
    }
```

```graphql
type Query {
    gerRandomNumbers: [Int!]!
}
```

```
curl --location 'http://localhost:8080/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"query randonNumber {\n    gerRandomNumbers\n}","variables":{}}'
```

### Returning a custom type

```graphql
query getBook {
    getBook {
        id
        title
    }
}

query getBook {
    getBook {
        title
    }
}

query getBook {
    getBook {
        id
    }
}
```

## Field Resolver In GraphQL

```
type Query {
    getPosts: [Post!]!
}

type BookResource {
    id: ID,
    title: String
}

type Post {
    id: ID
    title: String!
    description: String
    author: User
}

type User {
    id: ID!
    name: String!
    posts: [Post!]!
}
```

```
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class PostResolver {

    @QueryMapping
    fun getPosts(): List<Post> {
        return listOf(
            Post(
                id = UUID.randomUUID(),
                title = "clean code",
                description = "Clean Code: A Handbook of Agile Software Craftsmanship."
            )
        )
    }
}
```

```
query readPosts {
    getPosts {
        id
        title
        description
        author {
            id
        }
    }
}

{
    "data": {
        "getPosts": [
            {
                "id": "91e82e32-8eb7-4d0e-a3c7-f37acb62e704",
                "title": "clean code",
                "description": "Clean Code: A Handbook of Agile Software Craftsmanship.",
                "author": null
            }
        ]
    }
}

```


```
curl --location 'http://localhost:8080/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"query readPosts {\n    getPosts {\n        id\n        title\n        description\n        author {\n            id\n        }\n    }\n}","variables":{}}'
```

#### field resolver

```graphql
query readPosts {
    getPosts {
        id
        title
        description
        author {
            id,
            name
            posts {
                id
                title
                description
            }
        }
    }
}
```

```json
{
  "data": {
    "getPosts": [
      {
        "id": "00000000-0000-0000-0000-000000000001",
        "title": "clean code",
        "description": "Clean Code: A Handbook of Agile Software Craftsmanship.",
        "author": {
          "id": "3df76aa0-2e57-419f-a9b0-93dd81b09f2f",
          "name": "Duke author of clean code",
          "posts": [
            {
              "id": "00000000-0000-0000-0000-000000000001",
              "title": "some post from 3df76aa0-2e57-419f-a9b0-93dd81b09f2f Duke author of clean code",
              "description": "na"
            },
            {
              "id": "00000000-0000-0000-0000-000000000002",
              "title": "some post from 3df76aa0-2e57-419f-a9b0-93dd81b09f2f Duke author of clean code",
              "description": "na"
            }
          ]
        }
      },
      {
        "id": "00000000-0000-0000-0000-000000000002",
        "title": "Real World Java EE Patterns",
        "description": "Real World Java EE Patterns.",
        "author": {
          "id": "105b5429-1ca8-4b11-8e2b-7cd008d05c97",
          "name": "Duke author of Real World Java EE Patterns",
          "posts": [
            {
              "id": "00000000-0000-0000-0000-000000000001",
              "title": "some post from 105b5429-1ca8-4b11-8e2b-7cd008d05c97 Duke author of Real World Java EE Patterns",
              "description": "na"
            },
            {
              "id": "00000000-0000-0000-0000-000000000002",
              "title": "some post from 105b5429-1ca8-4b11-8e2b-7cd008d05c97 Duke author of Real World Java EE Patterns",
              "description": "na"
            }
          ]
        }
      }
    ]
  }
}
```

## Pagination With GraphQL

```
query mostPopularPosts {
    mostPopularPosts(page: 0, size: 2) {
        id
        title
        votes
        description
        author {
            id
            name
            posts {
                title
                id
                votes
            }
        }
    }
}
```

```
query mostPopularPosts {
    mostPopularPosts(page: 0, size: 2) {
        id
        title
        votes
        description
        author {
            id
            name
            posts {
                title
                id
                votes
            }
        }
    }
}
```

# Mutations

```
mutation addUserMutationName {
   addUser(addUserInput: { 
       name: "JC" 
       })
}
```

```
curl --location 'http://localhost:8080/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"mutation addUserMutationName {\n   addUser(addUserInput: { \n       name: \"JC\" \n       })\n}","variables":{}}'
```

## Users page

```graphql
query getuserspage {
   getUsers(page: 0 size: 2) {
       content {
           id
           name
           posts {
               id
               title
           }
       }
       isFirst
       isLast
       totalPages
       pageSize
       pageNumber

   }
}
```


## add new post record

```graphql
mutation addPostOperation {
    addPost (addPostInput: {
        title: "How to"
        description: "lero lero"
        authorId: "3ad1498a-6755-43b3-bd15-0e6720180fe1"
    }) {
        id
        title
        description
        author {
            id
            name
        }
    }
}
```
