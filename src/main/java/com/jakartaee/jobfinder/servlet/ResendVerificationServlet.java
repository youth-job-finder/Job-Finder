package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.services.UserService;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/resend-verification")
public class ResendVerificationServlet extends HttpServlet {

    private static final String SERVLET_NAME = "ResendVerificationServlet";

    @Inject
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String email = req.getParameter("email");
        
        MainLogger.logUserAction(SERVLET_NAME, email, "RESEND_VERIFICATION_REQUEST");

        // Set response content type for JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "RESEND_VERIFICATION", "Missing email", "anonymous");
            response.put("success", false);
            response.put("message", "Email address is required");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), response);
            return;
        }

        try {
            String baseUrl = getBaseUrl(req);
            boolean sent = userService.resendVerificationEmail(email.trim().toLowerCase(), baseUrl);
            
            if (sent) {
                MainLogger.logServiceOperation(SERVLET_NAME, "RESEND_VERIFICATION", true, "Email: " + email);
                response.put("success", true);
                response.put("message", "Verification email sent successfully! Please check your inbox.");
            } else {
                MainLogger.logAuthenticationError(SERVLET_NAME, "RESEND_VERIFICATION", "Failed to send", email);
                response.put("success", false);
                response.put("message", "Failed to send verification email. Please try again later.");
            }
            
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "RESEND_VERIFICATION", e.getMessage(), email);
            response.put("success", false);
            response.put("message", "An error occurred. Please try again later.");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        objectMapper.writeValue(resp.getWriter(), response);
    }
    
    private String getBaseUrl(HttpServletRequest req) {
        String scheme = req.getScheme();
        String serverName = req.getServerName();
        int serverPort = req.getServerPort();
        String contextPath = req.getContextPath();
        
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(scheme).append("://").append(serverName);
        if (serverPort != 80 && serverPort != 443) {
            baseUrl.append(":").append(serverPort);
        }
        baseUrl.append(contextPath);
        return baseUrl.toString();
    }
}
