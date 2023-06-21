package io.github.jlmc.acidrx.api.mappers;

import io.github.jlmc.acidrx.api.model.response.PostResource;
import io.github.jlmc.acidrx.domain.entities.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PostResourceMapper {

    PostResource postToPostResource(Post entity);
}
