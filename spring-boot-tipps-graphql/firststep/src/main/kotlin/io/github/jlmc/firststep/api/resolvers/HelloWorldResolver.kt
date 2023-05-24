package io.github.jlmc.firststep.api.resolvers

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class HelloWorldResolver {

    @QueryMapping
    fun helloworld(): String {
        return "Hello World!"
    }
}
