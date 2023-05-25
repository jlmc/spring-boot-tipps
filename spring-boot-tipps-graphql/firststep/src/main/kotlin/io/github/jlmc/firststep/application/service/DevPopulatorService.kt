package io.github.jlmc.firststep.application.service

import io.github.jlmc.firststep.application.port.out.UserRepository
import io.github.jlmc.firststep.domain.Post
import io.github.jlmc.firststep.domain.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Profile("!production")
class DevPopulatorService : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var userRepository: UserRepository

    @Transactional(propagation = Propagation.REQUIRED)
    override fun run(vararg args: String?) {
        logger.info("Running dev db population with args: ${args.contentToString()}")

        userRepository.deleteAll()
        userRepository.flush()

        val robert = User(name = "Robert")
        val adam = User(name = "Adam")
        val mickael = User(name = "Mickael")

        robert.addPosts(createPostsForTheAuthor(robert, 3))
        adam.addPosts(createPostsForTheAuthor(robert, 2))

        userRepository.saveAll(listOf(robert, adam, mickael))

        userRepository.flush()
    }

    private fun createPostsForTheAuthor(author: User, limit: Long = 3L): List<Post> {
        return (0..limit)
            .map {
                Post(
                    id = null,
                    title = "post $it from author ${author.name}",
                    description = "lero lero!!!",
                    author = author,
                    votes = it.toInt()
                )
            }
    }
}
