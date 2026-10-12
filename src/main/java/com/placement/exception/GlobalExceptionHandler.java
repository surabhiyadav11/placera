package com.placement.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * GlobalExceptionHandler - Centralized application-wide exception handler for MVC controllers.
 *
 * Demonstrates:
 * - OOP Exception Handling: Captures domain-specific exceptions and renders user-friendly error views.
 * - Prevents raw stack-trace leaks during viva demonstrations and production runtime.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("statusCode", 404);
        model.addAttribute("errorTitle", "Resource Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public String handleUnauthorizedAccess(UnauthorizedAccessException ex, Model model) {
        model.addAttribute("statusCode", 403);
        model.addAttribute("errorTitle", "Access Denied");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResource(DuplicateResourceException ex, Model model) {
        model.addAttribute("statusCode", 409);
        model.addAttribute("errorTitle", "Duplicate Entry Conflict");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, Model model) {
        model.addAttribute("statusCode", 413);
        model.addAttribute("errorTitle", "File Size Exceeded");
        model.addAttribute("errorMessage", "The uploaded resume exceeds the maximum permitted file size (5MB).");
        return "error/error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("statusCode", 400);
        model.addAttribute("errorTitle", "Invalid Request");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("statusCode", 500);
        model.addAttribute("errorTitle", "Application Error");
        model.addAttribute("errorMessage", "An unexpected condition occurred: " + ex.getMessage());
        return "error/error";
    }
}
