package io.github.jlmc.acidrx.domain.repositories;

import io.github.jlmc.acidrx.domain.entities.Post;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PostRepository extends R2dbcRepository<Post, UUID> {
    @MappedCollection(idColumn = "author_id")
    Flux<Post> findAllByAuthorIdOrderByAuthorId(UUID authorId);

    Mono<Post> findByIdAndAuthorId(UUID id, UUID authorId);
}
