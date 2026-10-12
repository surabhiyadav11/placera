package com.placement.exception;

/**
 * UnauthorizedAccessException - Thrown when a user attempts an action outside their
 * role or attempts to access another user's private domain resources.
 *
 * Demonstrates:
 * - OOP Inheritance: Subclasses RuntimeException.
 * - Server-side security enforcement: Prevents unauthorized horizontal and vertical access.
 * - Encapsulation of security violation details.
 */
public class UnauthorizedAccessException extends RuntimeException {

    private final String action;
    private final String reason;

    public UnauthorizedAccessException(String message) {
        super(message);
        this.action = "ACCESS";
        this.reason = message;
    }

    public UnauthorizedAccessException(String action, String reason) {
        super(String.format("Unauthorized action [%s]: %s", action, reason));
        this.action = action;
        this.reason = reason;
    }

    public String getAction() {
        return action;
    }

    public String getReason() {
        return reason;
    }
}
