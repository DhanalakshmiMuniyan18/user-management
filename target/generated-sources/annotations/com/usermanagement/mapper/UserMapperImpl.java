package com.usermanagement.mapper;

import com.usermanagement.dto.UserDto;
import com.usermanagement.model.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-23T23:31:35+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Ubuntu)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setRoleNames( rolesToRoleNames( user.getRoles() ) );
        userDto.setId( user.getId() );
        userDto.setName( user.getName() );
        userDto.setEmail( user.getEmail() );
        userDto.setPassword( user.getPassword() );
        userDto.setStatus( user.getStatus() );
        userDto.setLastLogin( user.getLastLogin() );
        userDto.setCreatedAt( user.getCreatedAt() );
        userDto.setUpdatedAt( user.getUpdatedAt() );

        return userDto;
    }

    @Override
    public User toEntity(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User user = new User();

        user.setId( userDto.getId() );
        user.setName( userDto.getName() );
        user.setEmail( userDto.getEmail() );
        user.setStatus( userDto.getStatus() );
        user.setLastLogin( userDto.getLastLogin() );
        user.setCreatedAt( userDto.getCreatedAt() );
        user.setUpdatedAt( userDto.getUpdatedAt() );
        user.setPassword( userDto.getPassword() );

        return user;
    }
}
