package com.userservice.management.rbac.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify required permissions for accessing a method.
 * @author Saravanamuthukumar S
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * The permissions required to access the method.
     */
    String[] value();

    /**
     * Whether all permissions are required (AND) or any permission is sufficient (OR).
     * Default is true (AND).
     */
    boolean allRequired() default true;
} 