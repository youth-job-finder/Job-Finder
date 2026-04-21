package com.jakartaee.jobfinder.security.session.filter;

import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter to detect expired sessions and redirect to login page.
 * This filter checks if the session has expired for protected routes
 * and redirects the user to the login page with an appropriate message.
 */
@WebFilter(filterName = "SessionExpirationFilter", urlPatterns = {
    "/company/*",
    "/admin/*",
    "/applicant/*",
    "/dashboard/*",
    "/view-cv",
    "/applicant/cv"
})
public class SessionExpirationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Filter initialization
        MainLogger.logServiceOperation("SessionExpirationFilter", "INIT",
                true, "Session expiration filter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        String requestURI = httpRequest.getRequestURI();
        
        // Check if session exists and is valid
        if (session == null || session.getAttribute("userId") == null) {
            // Session expired or user not logged in
            // Check if this is an AJAX request
            String requestedWith = httpRequest.getHeader("X-Requested-With");
            boolean isAjax = "XMLHttpRequest".equals(requestedWith);
            
            if (isAjax) {
                // Return 401 for AJAX requests
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\":\"Session expired\",\"redirect\":\"/login?sessionExpired=true\"}");
            } else {
                // Redirect to login page for regular requests
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?sessionExpired=true");
            }
            
            MainLogger.logUserAction("SessionExpirationFilter", "anonymous", 
                    "SESSION_EXPIRED_REDIRECT: " + requestURI);
            return;
        }
        
        // Check if session is about to expire (within 5 minutes)
        long lastAccessedTime = session.getLastAccessedTime();
        int maxInactiveInterval = session.getMaxInactiveInterval();
        long currentTime = System.currentTimeMillis();
        long timeSinceLastAccess = (currentTime - lastAccessedTime) / 1000; // in seconds
        long timeRemaining = maxInactiveInterval - timeSinceLastAccess;
        
        if (timeRemaining < 300) { // Less than 5 minutes remaining
            // Add warning header for client-side handling
            httpResponse.setHeader("X-Session-Warning", "Session expiring soon: " + timeRemaining + " seconds remaining");
        }
        
        // Continue with the request
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup
        MainLogger.logServiceOperation("SessionExpirationFilter", "DESTROY",
                true, "Session expiration filter destroyed");
    }
}
