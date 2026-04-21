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

@WebServlet({"/request-password-reset", "/reset-password"})
public class PasswordResetServlet extends HttpServlet {

    private static final String SERVLET_NAME = "PasswordResetServlet";

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String token = req.getParameter("token");
        
        if (token == null || token.trim().isEmpty()) {
            // Show password reset request form
            req.getRequestDispatcher("/views/password-reset-request.jsp").forward(req, resp);
        } else {
            // Show password reset form with token
            req.setAttribute("token", token.trim());
            req.getRequestDispatcher("/views/password-reset.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String servletPath = req.getServletPath();
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();

        if ("/request-password-reset".equals(servletPath)) {
            // Handle password reset request
            String email = req.getParameter("email");
            
            MainLogger.logUserAction(SERVLET_NAME, email, "PASSWORD_RESET_REQUEST");

            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email address is required");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), response);
                return;
            }

            try {
                String baseUrl = getBaseUrl(req);
                boolean sent = userService.requestPasswordReset(email.trim().toLowerCase(), baseUrl);
                
                if (sent) {
                    MainLogger.logServiceOperation(SERVLET_NAME, "PASSWORD_RESET_REQUEST", true, "Email: " + email);
                    response.put("success", true);
                    response.put("message", "Password reset link sent! Please check your email.");
                } else {
                    MainLogger.logAuthenticationError(SERVLET_NAME, "PASSWORD_RESET_REQUEST", "Failed to send", email);
                    response.put("success", false);
                    response.put("message", "Failed to send password reset email. Please try again later.");
                }
                
            } catch (Exception e) {
                MainLogger.logAuthenticationError(SERVLET_NAME, "PASSWORD_RESET_REQUEST", e.getMessage(), email);
                response.put("success", false);
                response.put("message", "An error occurred. Please try again later.");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } else if ("/reset-password".equals(servletPath)) {
            // Handle password reset
            String token = req.getParameter("token");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");
            
            MainLogger.logUserAction(SERVLET_NAME, "anonymous", "PASSWORD_RESET_ATTEMPT");

            if (token == null || token.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Invalid reset token");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), response);
                return;
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "New password is required");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), response);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "Passwords do not match");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), response);
                return;
            }

            if (!userService.isValidPassword(newPassword)) {
                response.put("success", false);
                response.put("message", userService.getPasswordRequirementsMessage());
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), response);
                return;
            }

            try {
                boolean reset = userService.resetPassword(token.trim(), newPassword);
                
                if (reset) {
                    MainLogger.logServiceOperation(SERVLET_NAME, "PASSWORD_RESET", true, "Token: " + token.substring(0, 8) + "...");
                    response.put("success", true);
                    response.put("message", "Password reset successfully! You can now login.");
                } else {
                    MainLogger.logAuthenticationError(SERVLET_NAME, "PASSWORD_RESET", "Invalid or expired token", "Token: " + token.substring(0, 8) + "...");
                    response.put("success", false);
                    response.put("message", "Invalid or expired reset token. Please request a new password reset.");
                }
                
            } catch (Exception e) {
                MainLogger.logAuthenticationError(SERVLET_NAME, "PASSWORD_RESET", e.getMessage(), "Token: " + token.substring(0, 8) + "...");
                response.put("success", false);
                response.put("message", "An error occurred. Please try again later.");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
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
