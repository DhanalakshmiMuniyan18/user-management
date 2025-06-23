package com.userservice.management.rbac.exception;

/**
 * Exception thrown when a requested resource is not found.
 * @author Saravanamuthukumar S
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
} 