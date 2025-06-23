package com.usermanagement.mapper;

import com.usermanagement.dto.AuditLogDto;
import com.usermanagement.model.entity.AuditLog;
import com.usermanagement.model.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-23T23:31:34+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Ubuntu)"
)
@Component
public class AuditLogMapperImpl implements AuditLogMapper {

    @Override
    public AuditLogDto toDto(AuditLog auditLog) {
        if ( auditLog == null ) {
            return null;
        }

        AuditLogDto auditLogDto = new AuditLogDto();

        auditLogDto.setUserId( auditLogUserId( auditLog ) );
        auditLogDto.setId( auditLog.getId() );
        auditLogDto.setAction( auditLog.getAction() );
        auditLogDto.setDetails( auditLog.getDetails() );

        return auditLogDto;
    }

    @Override
    public AuditLog toEntity(AuditLogDto auditLogDto) {
        if ( auditLogDto == null ) {
            return null;
        }

        AuditLog auditLog = new AuditLog();

        auditLog.setCreatedAt( auditLogDto.getCreatedAt() );
        auditLog.setId( auditLogDto.getId() );
        auditLog.setAction( auditLogDto.getAction() );
        auditLog.setDetails( auditLogDto.getDetails() );

        return auditLog;
    }

    private Long auditLogUserId(AuditLog auditLog) {
        if ( auditLog == null ) {
            return null;
        }
        User user = auditLog.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
