package com.jakartaee.jobfinder.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BusinessLogger {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logOperation(String component, String operation, String userId, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] BUSINESS_OP - Component: %s, Operation: %s, UserID: %s, Details: %s", 
            timestamp, component, operation, userId, details);
        System.out.println(message);
    }

    public static void logDataAccess(String component, String entity, int count, String userId) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] DATA_ACCESS - Component: %s, Entity: %s, Count: %d, UserID: %s", 
            timestamp, component, entity, count, userId);
        System.out.println(message);
    }

    public static void logPageView(String servlet, String pageName, String userId, boolean isAuthenticated) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String authStatus = isAuthenticated ? "AUTH" : "ANON";
        String message = String.format("[%s] PAGE_VIEW - Servlet: %s, Page: %s, UserID: %s, Status: %s", 
            timestamp, servlet, pageName, userId, authStatus);
        System.out.println(message);
    }

    public static void logError(String component, String operation, String error, String userId) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = String.format("[%s] BUSINESS_ERROR - Component: %s, Operation: %s, Error: %s, UserID: %s", 
            timestamp, component, operation, error, userId);
        System.err.println(message);
    }
}
