package com.usermanagement.mapper;

import com.usermanagement.dto.UserDto;
import com.usermanagement.model.entity.Role;
import com.usermanagement.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Saravanamuthukumar S
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roleNames", source = "roles", qualifiedByName = "rolesToRoleNames")
    UserDto toDto(User user);

    @Mapping(target = "roles", ignore = true)
    User toEntity(UserDto userDto);

    @Named("rolesToRoleNames")
    default Set<String> rolesToRoleNames(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
    }
} 