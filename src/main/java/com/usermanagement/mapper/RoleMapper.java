package com.usermanagement.mapper;

import com.usermanagement.dto.RoleDto;
import com.usermanagement.model.entity.Permission;
import com.usermanagement.model.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Saravanamuthukumar S
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionsToNames")
    RoleDto toDto(Role role);

    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleDto roleDto);

    @Named("permissionsToNames")
    default Set<String> permissionsToNames(Set<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        return permissions.stream()
            .map(Permission::getName)
            .collect(Collectors.toSet());
    }
} 