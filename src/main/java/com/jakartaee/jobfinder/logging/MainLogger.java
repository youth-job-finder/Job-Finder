package com.jakartaee.jobfinder.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Centralized logging utility for the JobFinder application.
 * Provides standardized logging methods for various system events including
 * authentication, user actions, service operations, and errors.
 */
public class MainLogger {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Logs a login attempt with the result.
     *
     * @param email    the email address attempting login
     * @param clientIP the client IP address
     * @param success  true if login succeeded, false otherwise
     */
    public static void logLoginAttempt(String email, String clientIP, boolean success) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String status = success ? "SUCCESS" : "FAILED";
        String message = String.format("[%s] LOGIN_%s - Email: %s, IP: %s", 
            timestamp, status, email, clientIP);
        System.out.println(message);
    }

    /**
     * Logs a user registration attempt with the result.
     *
     * @param email    the email address registering
     * @param name     the user's name
     * @param clientIP the client IP address
     * @param success  true if registration succeeded, false otherwise
     */
    public static void logRegistrationAttempt(String email, String name, String clientIP, boolean success) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String status = success ? "SUCCESS" : "FAILED";
        String message = String.format("[%s] REGISTRATION_%s - Email: %s, Name: %s, IP: %s", 
            timestamp, status, email, name, clientIP);
        System.out.println(message);
    }

    /**
     * Logs a user logout event.
     *
     * @param userId   the user ID
     * @param email    the user email
     * @param clientIP the client IP address
     */
    public static void logLogout(String userId, String email, String clientIP) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] LOGOUT - UserID: %s, Email: %s, IP: %s", 
            timestamp, userId, email, clientIP);
        System.out.println(message);
    }

    /**
     * Logs a security-related event.
     *
     * @param component the component where the event occurred
     * @param event     the event type
     * @param details   additional details about the event
     */
    public static void logSecurityEvent(String component, String event, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] SECURITY_EVENT - Component: %s, Event: %s, Details: %s", 
            timestamp, component, event, details);
        System.out.println(message);
    }

    /**
     * Logs an authentication error.
     *
     * @param component the component where the error occurred
     * @param operation the operation being performed
     * @param error     the error message
     * @param email     the user email (if available)
     */
    public static void logAuthenticationError(String component, String operation, String error, String email) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] AUTH_ERROR - Component: %s, Operation: %s, Error: %s, Email: %s", 
            timestamp, component, operation, error, email);
        System.err.println(message);
    }

    /**
     * Logs a user action.
     *
     * @param component the component handling the action
     * @param userId    the user ID
     * @param action    the action description
     */
    public static void logUserAction(String component, String userId, String action) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] USER_ACTION - Component: %s, UserID: %s, Action: %s", 
            timestamp, component, userId, action);
        System.out.println(message);
    }

    /**
     * Logs a service operation with its result.
     *
     * @param service   the service name
     * @param operation the operation name
     * @param success   true if operation succeeded, false otherwise
     * @param details   additional details
     */
    public static void logServiceOperation(String service, String operation, boolean success, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String status = success ? "SUCCESS" : "FAILED";
        String message = String.format("[%s] SERVICE_%s - Service: %s, Operation: %s, Details: %s", 
            timestamp, status, service, operation, details);
        System.out.println(message);
    }

    /**
     * Logs an informational message.
     *
     * @param component the component logging the message
     * @param message   the message to log
     */
    public static void logInfo(String component, String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String formatted = String.format("[%s] INFO - Component: %s, Message: %s",
            timestamp, component, message);
        System.out.println(formatted);
    }

    /**
     * Logs an error message.
     *
     * @param component the component logging the error
     * @param message   the error message
     */
    public static void logError(String component, String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String formatted = String.format("[%s] ERROR - Component: %s, Message: %s",
            timestamp, component, message);
        System.err.println(formatted);
    }

    /**
     * Logs an error message with exception details.
     *
     * @param component the component logging the error
     * @param message   the error message
     * @param error     the exception that occurred
     */
    public static void logError(String component, String message, Throwable error) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String formatted = String.format("[%s] ERROR - Component: %s, Message: %s, Error: %s",
            timestamp, component, message, error != null ? error.getMessage() : "N/A");
        System.err.println(formatted);
        if (error != null) {
            error.printStackTrace();
        }
    }
}
