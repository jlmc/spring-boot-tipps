package io.github.jlmc.acidrx.domain.service;

import io.github.jlmc.acidrx.domain.entities.Post;
import io.github.jlmc.acidrx.domain.entities.User;
import io.github.jlmc.acidrx.domain.repositories.PostRepository;
import io.github.jlmc.acidrx.domain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class AddNewUserPostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddNewUserPostService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public AddNewUserPostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Mono<String> execute(AddNewUserPostCommand command) {
        return userRepository.findById(UUID.fromString(command.userId()))
                .doFirst(() -> LOGGER.info("find the user to be the owner of the new posts"))
                .doOnSuccess((t) -> LOGGER.info("found the user to be the owner of the new posts: < {}, {}>", t.getId(), t.getName()))
                .zipWhen(user -> {
                    Post post = new Post();
                    post.setAuthorId(user.getId());
                    post.setTitle(command.title());
                    post.setDescription(command.description());
                    return Mono.just(post);
                })
                .map(tuple -> {
                    tuple.getT1().setNotes("XXX-" + Instant.now());

                    return Pair.of(tuple.getT1(), tuple.getT2());
                })
                .flatMap(pair -> saveUser(pair).then(savePost(pair)))
                .doOnSuccess((t) -> LOGGER.info("new post <{}> written by the author <{}> add successful", t.getId(), t.getAuthorId()))
                .map(Post::getId)
                .map(UUID::toString);
    }

    private Mono<Post> savePost(Pair<User, Post> pair) {
        return postRepository.save(pair.getSecond());
    }

    private Mono<User> saveUser(Pair<User, Post> pair) {
        return userRepository.save(pair.getFirst());
    }
}
