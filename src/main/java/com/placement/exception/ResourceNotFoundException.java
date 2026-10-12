package com.placement.exception;

/**
 * ResourceNotFoundException - Thrown when a requested domain entity cannot be located.
 *
 * Demonstrates OOP Exception Handling & Inheritance:
 * - Extends RuntimeException to provide unchecked domain exception handling.
 * - Constructor overloading provides flexibility for callers.
 * - Encapsulates resource metadata (resourceName, fieldName, fieldValue).
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = "Resource";
        this.fieldName = "identifier";
        this.fieldValue = null;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
