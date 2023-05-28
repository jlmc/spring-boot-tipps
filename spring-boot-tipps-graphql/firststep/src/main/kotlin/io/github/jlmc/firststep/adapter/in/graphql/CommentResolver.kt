package io.github.jlmc.firststep.adapter.`in`.graphql

import io.github.jlmc.firststep.adapter.`in`.graphql.model.CommentResource
import io.github.jlmc.firststep.application.port.`in`.GetCommentsCommand
import io.github.jlmc.firststep.application.port.`in`.GetCommentsUseCase
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class CommentResolver(private val getCommentsUseCase: GetCommentsUseCase) {

    @QueryMapping
    fun getComments(
        @Argument(name = "authorId") authorId: UUID?,
        @Argument(name = "postId") postId: UUID?
    ): List<CommentResource> {
        return getCommentsUseCase.getComments(GetCommentsCommand(postId = postId, authorId = authorId))
            .map(::CommentResource)
    }
}
