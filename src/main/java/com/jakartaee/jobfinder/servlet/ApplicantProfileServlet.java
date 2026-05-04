package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/applicant/profile")
public class ApplicantProfileServlet extends HttpServlet {

    @Inject
    private UserDAO userDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Enforce authentication - no redirects, return 401 if not authenticated
        String userId = (String) req.getSession().getAttribute("userId");
        if (userId == null && req.getUserPrincipal() != null) {
            userId = req.getUserPrincipal().getName();
            req.getSession().setAttribute("userId", userId);
        }
        
        if (userId == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        // Check authorization - must have APPLICANT role (from database)
        User user = userDAO.findById(userId);
        if (user == null || user.getRole() != Role.APPLICANT) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied - Applicant role required");
            return;
        }

        // Check email verification for viewing profile
        if (!user.getEmailVerified()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Email verification required. Please verify your email to access your profile.");
            return;
        }

        // Set user in request
        req.setAttribute("user", user);
            
        // Set authentication attributes for navbar
        req.setAttribute("isAuthenticated", true);
        req.setAttribute("userRole", "APPLICANT");

        req.getRequestDispatcher("/views/applicant/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Enforce authentication - no redirects, return 401 if not authenticated
        String userId = (String) req.getSession().getAttribute("userId");
        if (userId == null && req.getUserPrincipal() != null) {
            userId = req.getUserPrincipal().getName();
            req.getSession().setAttribute("userId", userId);
        }
        
        if (userId == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        // Check authorization - must have APPLICANT role (from database)
        User user = userDAO.findById(userId);
        if (user == null || user.getRole() != Role.APPLICANT) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied - Applicant role required");
            return;
        }

        // Check email verification for updating profile
        if (!user.getEmailVerified()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Email verification required. Please verify your email before updating your profile.");
            return;
        }

        // Check action parameter to determine what to do
        String action = req.getParameter("action");
        if ("changePassword".equals(action)) {
            handlePasswordChange(req, resp, user);
            return;
        }

        // Update user profile
        user.setName(req.getParameter("fullName"));
        user.setPhone(req.getParameter("phone"));
        user.setAddress(req.getParameter("address"));
        userDAO.update(user);
        req.setAttribute("successMessage", "Profile updated successfully!");
        req.setAttribute("user", user);
        
        // Update session data
        req.getSession().setAttribute("userName", user.getName());
            
        // Set authentication attributes for navbar
        req.setAttribute("isAuthenticated", true);
        req.setAttribute("userRole", "APPLICANT");

        req.getRequestDispatcher("/views/applicant/profile.jsp").forward(req, resp);
    }

    private void handlePasswordChange(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        // Validate passwords
        if (currentPassword == null || newPassword == null || confirmPassword == null) {
            req.setAttribute("errorMessage", "All password fields are required");
            forwardToProfile(req, resp, user);
            return;
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "New password and confirmation do not match");
            forwardToProfile(req, resp, user);
            return;
        }

        // Check minimum length
        if (newPassword.length() < 8) {
            req.setAttribute("errorMessage", "New password must be at least 8 characters");
            forwardToProfile(req, resp, user);
            return;
        }

        // Verify current password
        if (!userDAO.verifyPassword(user.getId(), currentPassword)) {
            req.setAttribute("errorMessage", "Current password is incorrect");
            forwardToProfile(req, resp, user);
            return;
        }

        // Update password
        userDAO.updatePassword(user.getId(), newPassword);
        req.setAttribute("successMessage", "Password changed successfully!");
        forwardToProfile(req, resp, user);
    }

    private void forwardToProfile(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        req.setAttribute("user", user);
        req.setAttribute("isAuthenticated", true);
        req.setAttribute("userRole", "APPLICANT");
        req.getRequestDispatcher("/views/applicant/profile.jsp").forward(req, resp);
    }
}
