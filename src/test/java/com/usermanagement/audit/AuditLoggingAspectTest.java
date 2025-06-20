package com.usermanagement.audit;

import com.usermanagement.model.entity.AuditLog;
import com.usermanagement.model.entity.User;
import com.usermanagement.repository.AuditLogRepository;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.security.UserPrincipal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Saravanamuthukumar S
 */
class AuditLoggingAspectTest {

    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JoinPoint joinPoint;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Signature signature;

    @InjectMocks
    private AuditLoggingAspect auditLoggingAspect;

    private UserPrincipal userPrincipal;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        userPrincipal = new UserPrincipal(1L, "test@example.com", "pass", null, true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void logAudit_logsWhenAuthenticatedAndUserFound() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("createUser");
        when(signature.toShortString()).thenReturn("UserServiceImpl.createUser(..)");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 2});

        auditLoggingAspect.logAudit(joinPoint, null);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLog log = captor.getValue();
        assertThat(log.getUser()).isEqualTo(user);
        assertThat(log.getAction()).isEqualTo("createUser");
        assertThat(log.getDetails()).contains("UserServiceImpl.createUser");
    }

    @Test
    void logAudit_doesNothingIfNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        auditLoggingAspect.logAudit(joinPoint, null);
        verify(auditLogRepository, never()).save(any());
    }

    @Test
    void logAudit_doesNothingIfUserNotFound() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        auditLoggingAspect.logAudit(joinPoint, null);
        verify(auditLogRepository, never()).save(any());
    }
} 