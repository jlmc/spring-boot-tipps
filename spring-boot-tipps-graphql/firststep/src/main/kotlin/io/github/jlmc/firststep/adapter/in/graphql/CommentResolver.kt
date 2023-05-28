package io.github.jlmc.firststep.adapter.`in`.graphql

import io.github.jlmc.firststep.adapter.`in`.graphql.model.CommentResource
import io.github.jlmc.firststep.adapter.`in`.graphql.model.PostResource
import io.github.jlmc.firststep.adapter.`in`.graphql.model.UserResource
import io.github.jlmc.firststep.application.port.`in`.GetCommentAuthorUserCase
import io.github.jlmc.firststep.application.port.`in`.GetCommentPostUserCase
import io.github.jlmc.firststep.application.port.`in`.GetCommentsCommand
import io.github.jlmc.firststep.application.port.`in`.GetCommentsUseCase
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class CommentResolver(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getCommentPostUserCase: GetCommentPostUserCase,
    private val getCommentAuthorUserCase: GetCommentAuthorUserCase,
) {

    @QueryMapping
    fun getComments(
        @Argument(name = "authorId") authorId: UUID?,
        @Argument(name = "postId") postId: UUID?
    ): List<CommentResource> {
        return getCommentsUseCase.getComments(GetCommentsCommand(postId = postId, authorId = authorId))
            .map(::CommentResource)
    }

    @SchemaMapping(typeName = "CommentResource", field = "author")
    fun author(commentResource: CommentResource): UserResource {
        return getCommentAuthorUserCase.getCommentAuthor(commentResource.id)
            ?.let(::UserResource) ?: throw IllegalStateException()
    }

    @SchemaMapping(typeName = "CommentResource", field = "post")
    fun post(commentResource: CommentResource): PostResource {
        return getCommentPostUserCase.getCommentPost(commentResource.id)
            ?.let(::PostResource) ?: throw IllegalStateException()
    }
}
