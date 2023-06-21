package io.github.jlmc.acidrx.api;

import io.github.jlmc.acidrx.api.mappers.PostResourceMapper;
import io.github.jlmc.acidrx.api.model.requests.AddUserPostRequest;
import io.github.jlmc.acidrx.api.model.response.PostResource;
import io.github.jlmc.acidrx.domain.service.AddNewUserPostCommand;
import io.github.jlmc.acidrx.domain.service.AddNewUserPostService;
import io.github.jlmc.acidrx.domain.service.GetUserPostQueryService;
import io.github.jlmc.acidrx.domain.service.GetUserPostsQueryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(
        value = "/users/{userId}/posts",
        produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class UserPostsController {

    private final GetUserPostsQueryService getUserPostsQueryService;
    private final AddNewUserPostService addNewUserPostService;
    private final GetUserPostQueryService getUserPostQueryService;
    private final PostResourceMapper postResourceMapper;

    public UserPostsController(GetUserPostsQueryService getUserPostsQueryService,
                               AddNewUserPostService addNewUserPostService,
                               GetUserPostQueryService getUserPostQueryService,
                               PostResourceMapper postResourceMapper) {
        this.getUserPostsQueryService = getUserPostsQueryService;
        this.addNewUserPostService = addNewUserPostService;
        this.getUserPostQueryService = getUserPostQueryService;
        this.postResourceMapper = postResourceMapper;
    }

    @GetMapping
    public Flux<PostResource> getUserPosts(@PathVariable("userId") String userId) {
        return getUserPostsQueryService.get(userId)
                .map(postResourceMapper::postToPostResource);
    }

    @GetMapping(path = "/{postId}")
    public Mono<PostResource> getUserPosts(@PathVariable("userId") String userId, @PathVariable("postId") String postId) {
        return getUserPostQueryService.query(userId, postId).map(postResourceMapper::postToPostResource);
    }

    @PostMapping
    public Mono<ResponseEntity<PostResource>> addNewPost(@PathVariable("userId") String userId,
                                                         @Validated @RequestBody AddUserPostRequest payload,
                                                         ServerHttpRequest request) {
        return addNewUserPostService.execute(new AddNewUserPostCommand(userId, payload.title(), payload.description()))
                .flatMap(postId -> this.getUserPostQueryService.query(userId, postId))
                .map(post -> {
                    var location = UriComponentsBuilder.fromHttpRequest(request)
                            .path("/{id}").build(post.getId());
                    return ResponseEntity.created(location).body(postResourceMapper.postToPostResource(post));
                });
    }
}
