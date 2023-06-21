package io.github.jlmc.acidrx.domain.service;

import io.github.jlmc.acidrx.domain.entities.Post;
import io.github.jlmc.acidrx.domain.entities.User;
import io.github.jlmc.acidrx.domain.repositories.PostRepository;
import io.github.jlmc.acidrx.domain.repositories.UserRepository;
import io.github.jlmc.acidrx.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class GetUserPostsQueryService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public GetUserPostsQueryService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Flux<Post> get(String authorId) {
        return findUser(authorId)
                .mapNotNull(User::getId)
                .flatMapMany(postRepository::findAllByAuthorIdOrderByAuthorId);
    }

    private Mono<User> findUser(String authorId) {
        return userRepository.findById(UUID.fromString(authorId))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("user '" + authorId + "' not found")));
    }
}
