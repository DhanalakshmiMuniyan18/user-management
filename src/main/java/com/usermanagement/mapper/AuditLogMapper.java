package com.usermanagement.mapper;

import com.usermanagement.dto.AuditLogDto;
import com.usermanagement.model.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Saravanamuthukumar S
 */
@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    @Mapping(target = "userId", source = "user.id")
    AuditLogDto toDto(AuditLog auditLog);

    @Mapping(target = "user", ignore = true)
    AuditLog toEntity(AuditLogDto auditLogDto);
} 