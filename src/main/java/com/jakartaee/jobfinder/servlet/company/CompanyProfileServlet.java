package com.jakartaee.jobfinder.servlet.company;

import com.jakartaee.jobfinder.dao.CompanyDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.Company;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet for managing Company Profile.
 * Handles viewing and updating company profile information.
 */
@WebServlet("/company/profile")
public class CompanyProfileServlet extends HttpServlet {

    private static final String SERVLET_NAME = "CompanyProfileServlet";

    @Inject
    private AuthService authService;

    @Inject
    private CompanyDAO companyDAO;

    @Inject
    private UserDAO userDAO;

    /**
     * Handles GET requests to display the company profile.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = authService.requireCompanyAdmin(req, resp, SERVLET_NAME);
        if (user == null) {
            return;
        }

        String currentUserId = user.getId();
        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_COMPANY_PROFILE");

        try {
            Company company = companyDAO.findByUserId(currentUserId);
            if (company == null) {
                req.setAttribute("error", "No company found for this user. Please register your company first.");
            } else {
                req.setAttribute("company", company);
            }

            req.setAttribute("isAuthenticated", true);
            req.setAttribute("role", Role.COMPANY_ADMIN.name());
            req.setAttribute("user", user);
            req.getRequestDispatcher("/views/company/profile.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_PROFILE", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load company profile");
        }
    }

    /**
     * Handles POST requests to update the company profile.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = authService.requireCompanyAdmin(req, resp, SERVLET_NAME);
        if (user == null) {
            return;
        }

        String currentUserId = user.getId();

        try {
            Company company = companyDAO.findByUserId(currentUserId);
            if (company == null) {
                req.setAttribute("error", "No company found for this user.");
                req.getRequestDispatcher("/views/company/profile.jsp").forward(req, resp);
                return;
            }

            // Check if this is a password change request
            String action = req.getParameter("action");
            if ("changePassword".equals(action)) {
                handlePasswordChange(req, resp, user, company);
                return;
            }

            // Get form parameters
            String companyName = req.getParameter("companyName");
            String companyEmail = req.getParameter("companyEmail");
            String companyUrl = req.getParameter("companyUrl");
            String registrationNumber = req.getParameter("registrationNumber");
            String description = req.getParameter("description");
            String industry = req.getParameter("industry");
            String location = req.getParameter("location");

            // Validate required fields
            if (companyName == null || companyName.trim().isEmpty() ||
                companyEmail == null || companyEmail.trim().isEmpty() ||
                companyUrl == null || companyUrl.trim().isEmpty() ||
                registrationNumber == null || registrationNumber.trim().isEmpty()) {
                
                req.setAttribute("error", "All fields are required.");
                req.setAttribute("company", company);
                req.setAttribute("isAuthenticated", true);
                req.setAttribute("role", Role.COMPANY_ADMIN.name());
                req.setAttribute("user", user);
                req.getRequestDispatcher("/views/company/profile.jsp").forward(req, resp);
                return;
            }

            // Update company fields
            company.setName(companyName.trim());
            company.setEmail(companyEmail.trim());
            company.setUrl(companyUrl.trim());
            company.setRegistrationNumber(registrationNumber.trim());
            company.setDescription(description != null ? description.trim() : null);
            company.setIndustry(industry != null ? industry.trim() : null);
            company.setLocation(location != null ? location.trim() : null);

            // Save to database
            companyDAO.update(company);

            MainLogger.logUserAction(SERVLET_NAME, currentUserId, "UPDATE_COMPANY_PROFILE");

            req.setAttribute("success", "Company profile updated successfully!");
            req.setAttribute("company", company);
            req.setAttribute("isAuthenticated", true);
            req.setAttribute("role", Role.COMPANY_ADMIN.name());
            req.setAttribute("user", user);
            req.getRequestDispatcher("/views/company/profile.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "UPDATE_PROFILE", e.getMessage(), currentUserId);
            req.setAttribute("error", "An error occurred while updating the profile. Please try again.");
            req.setAttribute("isAuthenticated", true);
            req.setAttribute("role", Role.COMPANY_ADMIN.name());
            req.setAttribute("user", user);
            req.getRequestDispatcher("/views/company/profile.jsp").forward(req, resp);
        }
    }

    private void handlePasswordChange(HttpServletRequest req, HttpServletResponse resp, User user, Company company) 
            throws ServletException, IOException {
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        // Validate passwords
        if (currentPassword == null || newPassword == null || confirmPassword == null) {
            req.setAttribute("error", "All password fields are required");
            forwardToProfile(req, resp, user, company);
            return;
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "New password and confirmation do not match");
            forwardToProfile(req, resp, user, company);
            return;
        }

        // Check minimum length
        if (newPassword.length() < 8) {
            req.setAttribute("error", "New password must be at least 8 characters");
            forwardToProfile(req, resp, user, company);
            return;
        }

        // Verify current password
        if (!userDAO.verifyPassword(user.getId(), currentPassword)) {
            req.setAttribute("error", "Current password is incorrect");
            forwardToProfile(req, resp, user, company);
            return;
        }

        // Update password
        userDAO.updatePassword(user.getId(), newPassword);
        MainLogger.logUserAction(SERVLET_NAME, user.getId(), "CHANGE_PASSWORD");

        req.setAttribute("success", "Password changed successfully!");
        forwardToProfile(req, resp, user, company);
    }

    private void forwardToProfile(HttpServletRequest req, HttpServletResponse resp, User user, Company company) 
            throws ServletException, IOException {
        req.setAttribute("company", company);
        req.setAttribute("user", user);
        req.setAttribute("isAuthenticated", true);
        req.setAttribute("role", Role.COMPANY_ADMIN.name());
        req.getRequestDispatcher("/views/company/profile.jsp").forward(req, resp);
    }
}
