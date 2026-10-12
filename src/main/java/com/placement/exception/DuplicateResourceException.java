package com.placement.exception;

/**
 * DuplicateResourceException - Thrown when an entity violates unique constraints
 * (e.g. duplicate email registration, duplicate roll number, or duplicate job application).
 *
 * Demonstrates:
 * - OOP Inheritance: Subclasses RuntimeException.
 * - Constructor Overloading: Multiple constructors for custom and formatted messages.
 * - Encapsulation of constraint violation metadata.
 */
public class DuplicateResourceException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public DuplicateResourceException(String message) {
        super(message);
        this.resourceName = "Resource";
        this.fieldName = "field";
        this.fieldValue = null;
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
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
