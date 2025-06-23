package com.usermanagement.mapper;

import com.usermanagement.dto.RoleDto;
import com.usermanagement.model.entity.Role;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-23T23:31:35+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Ubuntu)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDto toDto(Role role) {
        if ( role == null ) {
            return null;
        }

        Set<String> permissions = null;
        Long id = null;
        String name = null;
        String description = null;

        permissions = permissionsToNames( role.getPermissions() );
        id = role.getId();
        name = role.getName();
        description = role.getDescription();

        RoleDto roleDto = new RoleDto( id, name, description, permissions );

        return roleDto;
    }

    @Override
    public Role toEntity(RoleDto roleDto) {
        if ( roleDto == null ) {
            return null;
        }

        Role role = new Role();

        role.setId( roleDto.id() );
        role.setName( roleDto.name() );
        role.setDescription( roleDto.description() );

        return role;
    }
}
