package io.github.jlmc.firststep.application.port.`in`


interface GetUserTotalPostsUseCase {

    fun getUserTotalPosts(command: GetUserTotalPostsCommand): Long

}
