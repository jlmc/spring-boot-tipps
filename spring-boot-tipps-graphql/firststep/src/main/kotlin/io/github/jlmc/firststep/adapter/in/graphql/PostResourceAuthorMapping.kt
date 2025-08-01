package io.github.jlmc.firststep.adapter.`in`.graphql

import io.github.jlmc.firststep.adapter.`in`.graphql.model.PostResource
import io.github.jlmc.firststep.adapter.`in`.graphql.model.UserResource
import io.github.jlmc.firststep.application.service.PostService
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class PostResourceAuthorMapping(
    private val postService: PostService
) {

    /**
     * this functions it is field resolver, it needs to be named as the name of the fields in the schema post.author
     * the typeName defines who is the parent of this field resolver, remember that the field resolver method must receive always the parent
     */
    @SchemaMapping(typeName = "PostResource") // the typeName defines who is the parent of this field resolver
    fun author(postResource: PostResource): UserResource {
        return postService.getAuthorByPostId(postResource.id)
            ?.let(::UserResource) ?: throw IllegalStateException()
    }
}
