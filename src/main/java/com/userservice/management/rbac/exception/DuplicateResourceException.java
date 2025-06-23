package com.userservice.management.rbac.exception;

/**
 * Exception thrown when attempting to create a duplicate resource.
 * @author Saravanamuthukumar S
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s : '%s'", resourceName, fieldName, fieldValue));
    }
} 