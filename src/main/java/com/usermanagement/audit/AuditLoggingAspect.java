package com.usermanagement.audit;

import com.usermanagement.model.entity.AuditLog;
import com.usermanagement.model.entity.User;
import com.usermanagement.repository.AuditLogRepository;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Saravanamuthukumar S
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLoggingAspect {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Pointcut("execution(* com.usermanagement.service.impl.*ServiceImpl.create*(..)) || " +
            "execution(* com.usermanagement.service.impl.*ServiceImpl.update*(..)) || " +
            "execution(* com.usermanagement.service.impl.*ServiceImpl.delete*(..)) || " +
            "execution(* com.usermanagement.service.impl.AccessRequestServiceImpl.approveRequest(..)) || " +
            "execution(* com.usermanagement.service.impl.AccessRequestServiceImpl.rejectRequest(..))")
    public void auditableActions() {}

    @AfterReturning(pointcut = "auditableActions()", returning = "result")
    public void logAudit(JoinPoint joinPoint, Object result) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        Object principal = authentication.getPrincipal();
        Long userId = null;
        if (principal instanceof UserPrincipal userPrincipal) {
            userId = userPrincipal.getId();
        }
        if (userId == null) {
            return;
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return;
        }
        String action = joinPoint.getSignature().getName();
        String details;
        if ("createUser".equals(action) && joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
            Object arg = joinPoint.getArgs()[0];
            String email = null;
            try {
                if (arg != null) {
                    java.lang.reflect.Method getEmail = arg.getClass().getMethod("getEmail");
                    Object emailObj = getEmail.invoke(arg);
                    if (emailObj != null) {
                        email = emailObj.toString();
                    }
                }
            } catch (Exception ignored) {}
            if (email != null) {
                details = "Created user with email: " + email;
            } else {
                details = buildDetails(joinPoint);
            }
        } else {
            details = buildDetails(joinPoint);
        }
        AuditLog log = new AuditLog();
        log.setUser(userOpt.get());
        log.setAction(action);
        log.setDetails(details);
        log.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    private String buildDetails(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        sb.append("Method: ").append(joinPoint.getSignature().toShortString());
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            sb.append(", Args: [");
            for (int i = 0; i < args.length; i++) {
                sb.append(args[i]);
                if (i < args.length - 1) sb.append(", ");
            }
            sb.append("]");
        }
        return sb.toString();
    }
} 