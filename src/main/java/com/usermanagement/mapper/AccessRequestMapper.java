package com.usermanagement.mapper;

import com.usermanagement.dto.AccessRequestDto;
import com.usermanagement.model.entity.AccessRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Saravanamuthukumar S
 */
@Mapper(componentModel = "spring")
public interface AccessRequestMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "roleId", source = "role.id")
    AccessRequestDto toDto(AccessRequest accessRequest);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "role", ignore = true)
    AccessRequest toEntity(AccessRequestDto accessRequestDto);
} 