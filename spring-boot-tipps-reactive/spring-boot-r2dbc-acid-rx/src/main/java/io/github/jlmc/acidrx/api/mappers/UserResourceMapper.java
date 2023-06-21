package io.github.jlmc.acidrx.api.mappers;

import io.github.jlmc.acidrx.api.model.response.UserResource;
import io.github.jlmc.acidrx.domain.entities.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface UserResourceMapper {

    UserResource userToUserResource(User user);
}
