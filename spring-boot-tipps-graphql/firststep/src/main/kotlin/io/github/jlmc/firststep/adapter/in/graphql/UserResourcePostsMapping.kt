package io.github.jlmc.firststep.adapter.`in`.graphql

import io.github.jlmc.firststep.adapter.`in`.graphql.model.PostResource
import io.github.jlmc.firststep.adapter.`in`.graphql.model.UserResource
import io.github.jlmc.firststep.application.port.`in`.GetUserTotalPostsUseCase
import io.github.jlmc.firststep.application.service.PostService
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class UserResourcePostsMapping(
    private val postService: PostService,
    private val getUserTotalPostsUser: GetUserTotalPostsUseCase,
) {

    val logger = LoggerFactory.getLogger(javaClass)

    /**
     * field resolver for `post.author.posts`
     */

    /*
    @SchemaMapping(typeName = "UserResource")
    fun posts(userResource: UserResource): List<PostResource> {
        return postService.getPostsAuthorId(userResource.id)
            .map(::PostResource)
    }
     */

    @BatchMapping(
        typeName = "UserResource",
        field = "posts"
    )
    fun batchAuthorPosts(userResources: List<UserResource>): Map<UserResource, List<PostResource>> {
        logger.debug("batching fetching authors posts")

        val userResourceIndex = userResourceIndex(userResources)
        val entities = postService.getPostsByAuthorIds(userResourceIndex.keys)
            .map { it.key.id!! to it.value }
            .toMap()

        return userResourceIndex.entries
            .associate { userREntry ->
                userREntry.value to (entities[userREntry.key]?.map(::PostResource) ?: emptyList())
            }
    }

    private fun userResourceIndex(userResources: Collection<UserResource>): Map<UUID, UserResource> {
        return userResources.toSet().associateBy(UserResource::id)
    }

    /*
    @SchemaMapping(typeName = "UserResource")
    fun totalPosts(userResource: UserResource): Long {
        return getUserTotalPostsUser.getUserTotalPosts(GetUserTotalPostsCommand(userId = userResource.id))
    }
     */

    @BatchMapping(typeName = "UserResource", field = "totalPosts")
    fun batchTotalPosts(userResources: Collection<UserResource>): Map<UserResource, Long> {
        logger.debug("batching fetching authors totalPosts")

        val userResourceIndex = userResourceIndex(userResources = userResources)
        val entities: Map<UUID, Long> = postService.getTotalPostByAuthor(userResourceIndex.keys)

        val results = userResourceIndex.values.associateWith {
            entities[it.id]!!
        }

        return results
    }


    /*
        /*
    @SchemaMapping(typeName = "PostResource", field = "comments")
    fun comments(postResource: PostResource): List<CommentResource> {
        return postService.getPostComments(postResource.id).map(::CommentResource)
    }
     */

    @BatchMapping(
        typeName = "PostResource",
        field = "comments"
    )
    fun batchPostComments(postResources: List<PostResource>): Map<PostResource, List<CommentResource>> {
        val postResourceByIdCache =
            postResources.toSet().associateBy(PostResource::id)

        val postComments =
            postService.getPostsComments(postResources.map(PostResource::id))
                .groupBy(Comment::post)

        return postComments.entries.associate {
            Pair(
                postResourceByIdCache[it.key.id]!!,
                it.value.map(::CommentResource)
            )
        }
    }
     */
}
