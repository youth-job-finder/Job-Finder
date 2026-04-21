package com.jakartaee.jobfinder.security.session.listener;

import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Session listener to handle session creation and expiration events.
 * Logs session activity and ensures proper cleanup when sessions expire.
 */
@WebListener
public class SessionTimeoutListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        // Session created - log for monitoring
        String sessionId = event.getSession().getId();
        int maxInactiveInterval = event.getSession().getMaxInactiveInterval();
        MainLogger.logServiceOperation("SessionTimeoutListener", "SESSION_CREATED",
                true, "Session ID: " + sessionId.substring(0, 8) + "..., Timeout: " + maxInactiveInterval + "s");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        // Session destroyed (expired or invalidated) - log and cleanup
        String sessionId = event.getSession().getId();
        Object userId = event.getSession().getAttribute("userId");
        Object userRole = event.getSession().getAttribute("userRole");
        
        if (userId != null) {
            MainLogger.logUserAction("SessionTimeoutListener", userId.toString(), 
                    "SESSION_EXPIRED: " + sessionId.substring(0, 8) + "...");
        } else {
            MainLogger.logServiceOperation("SessionTimeoutListener", "SESSION_DESTROYED",
                    true, "Anonymous session: " + sessionId.substring(0, 8) + "...");
        }
        
        // Ensure all session attributes are cleared
        event.getSession().removeAttribute("userId");
        event.getSession().removeAttribute("userRole");
        event.getSession().removeAttribute("isAuthenticated");
        event.getSession().removeAttribute("cvFileName");
    }
}
