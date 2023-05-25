package io.github.jlmc.firststep.api.resolvers.posts

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import java.util.UUID

@Controller
class PostResolver {

    @QueryMapping
    fun getPosts(): List<PostResource> {
        return listOf(
            PostResource(
                id = UUID(0L, 1L),
                title = "clean code",
                description = "Clean Code: A Handbook of Agile Software Craftsmanship."
            ),
            PostResource(
                id = UUID(0L, 2L),
                title = "Real World Java EE Patterns",
                description = "Real World Java EE Patterns."
            )
        )
    }

    /**
     * this functions it is field resolver, it needs to be named as the name of the fields in the schema post.author
     * the typeName defines who is the parent of this field resolver, remember that the field resolver method must receive always the parent
     */
    @SchemaMapping(typeName = "PostResource") // the typeName defines who is the parent of this field resolver
    fun author(postResource: PostResource): UserResource {
        return UserResource(id = UUID.randomUUID(), name = "Duke author of " + postResource.title)
    }

    /**
     * field resolver for `post.author.posts`
     */
    @SchemaMapping(typeName = "UserResource")
    fun posts(userResource: UserResource): List<PostResource> {
        val postResources =
            (1..2).map {
                PostResource(
                    id = UUID(0, it.toLong()),
                    title = "some post from ${userResource.id} ${userResource.name}",
                    description = "na"
                )
            }

        return postResources
    }
}
