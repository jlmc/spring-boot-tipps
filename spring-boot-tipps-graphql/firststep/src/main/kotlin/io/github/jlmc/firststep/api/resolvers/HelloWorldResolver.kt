package io.github.jlmc.firststep.api.resolvers

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class HelloWorldResolver {

    @QueryMapping
    fun helloworld(): String {
        return "Hello World!"
    }

    /**
     * greet(name: String!): String!
     */
    @QueryMapping
    fun greet(@Argument name: String): String {
        return "Hello $name"
    }

    /**
     * gerRandomNumbers: [Int!]!
     */
    @QueryMapping
    fun gerRandomNumbers(): List<Int> {
        return listOf(1, 2, 3)
    }
}
