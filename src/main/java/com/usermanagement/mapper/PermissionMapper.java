package com.usermanagement.mapper;

import com.usermanagement.dto.PermissionDto;
import com.usermanagement.model.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Saravanamuthukumar S
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionDto toDto(Permission permission);

    @Mapping(target = "roles", ignore = true)
    Permission toEntity(PermissionDto permissionDto);
} 