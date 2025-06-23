package com.usermanagement.mapper;

import com.usermanagement.dto.AccessRequestDto;
import com.usermanagement.model.entity.AccessRequest;
import com.usermanagement.model.entity.Role;
import com.usermanagement.model.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-23T15:41:31+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Ubuntu)"
)
@Component
public class AccessRequestMapperImpl implements AccessRequestMapper {

    @Override
    public AccessRequestDto toDto(AccessRequest accessRequest) {
        if ( accessRequest == null ) {
            return null;
        }

        AccessRequestDto accessRequestDto = new AccessRequestDto();

        accessRequestDto.setUserId( accessRequestUserId( accessRequest ) );
        accessRequestDto.setRoleId( accessRequestRoleId( accessRequest ) );
        accessRequestDto.setId( accessRequest.getId() );
        accessRequestDto.setStatus( accessRequest.getStatus() );
        accessRequestDto.setReason( accessRequest.getReason() );
        accessRequestDto.setResponseMessage( accessRequest.getResponseMessage() );
        accessRequestDto.setCreatedAt( accessRequest.getCreatedAt() );
        accessRequestDto.setUpdatedAt( accessRequest.getUpdatedAt() );

        return accessRequestDto;
    }

    @Override
    public AccessRequest toEntity(AccessRequestDto accessRequestDto) {
        if ( accessRequestDto == null ) {
            return null;
        }

        AccessRequest.AccessRequestBuilder accessRequest = AccessRequest.builder();

        accessRequest.id( accessRequestDto.getId() );
        accessRequest.status( accessRequestDto.getStatus() );
        accessRequest.reason( accessRequestDto.getReason() );
        accessRequest.responseMessage( accessRequestDto.getResponseMessage() );
        accessRequest.createdAt( accessRequestDto.getCreatedAt() );
        accessRequest.updatedAt( accessRequestDto.getUpdatedAt() );

        return accessRequest.build();
    }

    private Long accessRequestUserId(AccessRequest accessRequest) {
        if ( accessRequest == null ) {
            return null;
        }
        User user = accessRequest.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long accessRequestRoleId(AccessRequest accessRequest) {
        if ( accessRequest == null ) {
            return null;
        }
        Role role = accessRequest.getRole();
        if ( role == null ) {
            return null;
        }
        Long id = role.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
