package io.github.jlmc.acidrx.domain.service;

import io.github.jlmc.acidrx.domain.entities.Post;
import io.github.jlmc.acidrx.domain.repositories.PostRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class GetUserPostQueryService {

    private final PostRepository postRepository;

    public GetUserPostQueryService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Mono<Post> query(String userId, String postId) {
        return this.postRepository.findByIdAndAuthorId(UUID.fromString(postId), UUID.fromString(userId));
    }
}
