package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.services.UserService;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/signup", "/user-signup"})
public class UserRegistrationServlet extends HttpServlet {

    private static final String SERVLET_NAME = "UserRegistrationServlet";

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        MainLogger.logUserAction(SERVLET_NAME, "anonymous", "VIEW_REGISTRATION_PAGE");
        req.getRequestDispatcher("/views/user-signup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String name = req.getParameter("fullname");
        String email = req.getParameter("email");
        
        try {
            String password = req.getParameter("password");
            String confirmPassword = req.getParameter("confirmPassword");
            String roleStr = req.getParameter("role");

            if (name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                
                MainLogger.logRegistrationAttempt(email, name, "unknown", false);
                req.setAttribute("error", "All fields are required");
                req.getRequestDispatcher("/views/user-signup.jsp").forward(req, resp);
                return;
            }

            if (!password.equals(confirmPassword)) {
                MainLogger.logRegistrationAttempt(email, name, "unknown", false);
                req.setAttribute("error", "Passwords do not match");
                req.getRequestDispatcher("/views/user-signup.jsp").forward(req, resp);
                return;
            }

            // Strong password validation
            if (!userService.isValidPassword(password)) {
                MainLogger.logRegistrationAttempt(email, name, "unknown", false);
                req.setAttribute("error", userService.getPasswordRequirementsMessage());
                req.getRequestDispatcher("/views/user-signup.jsp").forward(req, resp);
                return;
            }

            Role role = Role.APPLICANT;
            if (roleStr != null && !roleStr.trim().isEmpty()) {
                try {
                    role = Role.valueOf(roleStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    MainLogger.logAuthenticationError(SERVLET_NAME, "REGISTER", "Invalid role specified", email);
                    req.setAttribute("error", "Invalid role specified");
                    req.getRequestDispatcher("/views/user-signup.jsp").forward(req, resp);
                    return;
                }
            }

            // Check for duplicate email
            if (userService.findUserByEmail(email.trim().toLowerCase()).isPresent()) {
                MainLogger.logRegistrationAttempt(email, name, "unknown", false);
                req.setAttribute("error", "An account with this email address already exists. Please use a different email or log in.");
                req.getRequestDispatcher("/views/user-signup.jsp").forward(req, resp);
                return;
            }

            String baseUrl = getBaseUrl(req);
            userService.registerUser(name.trim(), email.trim().toLowerCase(), password, role, baseUrl);
            MainLogger.logRegistrationAttempt(email, name, "unknown", true);

            req.setAttribute("success", "Registration successful! Please check your email to verify your account.");
            resp.sendRedirect(req.getContextPath() + "/login?registered=true&verificationRequired=true");

        } catch (IllegalArgumentException e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "REGISTER", e.getMessage(), email);
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/views/user-signup.jsp").forward(req, resp);
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "REGISTER", e.getMessage(), email);
            req.setAttribute("error", "Registration failed. Please try again.");
            req.getRequestDispatcher("/views/user-signup.jsp").forward(req, resp);
        }
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
