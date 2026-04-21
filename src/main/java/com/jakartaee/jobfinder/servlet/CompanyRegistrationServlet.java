package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.entity.Company;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.CompanyRegistrationService;
import com.jakartaee.jobfinder.services.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/company-register")
public class CompanyRegistrationServlet extends HttpServlet {

    private static final String SERVLET_NAME = "CompanyRegistrationServlet";

    @Inject
    private CompanyRegistrationService companyRegistrationService;

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        MainLogger.logUserAction(SERVLET_NAME, "anonymous", "VIEW_COMPANY_REGISTRATION_PAGE");

        try {
            req.getRequestDispatcher("/views/company-register.jsp").forward(req, resp);
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_REGISTRATION_PAGE",
                    e.getMessage(), "anonymous");
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load registration page");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Admin (User) Information
        String adminName = req.getParameter("adminName");
        String adminEmail = req.getParameter("adminEmail");
        String adminPassword = req.getParameter("adminPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        // Company Information
        String companyName = req.getParameter("companyName");
        String registrationNumber = req.getParameter("registrationNumber");
        String companyEmail = req.getParameter("companyEmail");
        String companyUrl = req.getParameter("companyUrl");
        String description = req.getParameter("description");
        String industry = req.getParameter("industry");
        String location = req.getParameter("location");

        MainLogger.logUserAction(SERVLET_NAME, "anonymous", "COMPANY_REGISTRATION_ATTEMPT");

        try {
            // Input validation
            validateInputs(adminName, adminEmail, adminPassword, confirmPassword,
                    companyName, registrationNumber, companyEmail);
            
            // Check for duplicate admin email
            if (userService.findUserByEmail(adminEmail.trim().toLowerCase()).isPresent()) {
                throw new IllegalArgumentException("An account with this admin email address already exists. Please use a different email.");
            }
            
            // Check for duplicate company email
            if (companyRegistrationService.isCompanyEmailExists(companyEmail.trim().toLowerCase())) {
                throw new IllegalArgumentException("A company with this email address already exists. Please use a different email.");
            }

            // Register company with admin
            String baseUrl = getBaseUrl(req);
            Company company = companyRegistrationService.registerCompanyWithAdmin(
                    adminName.trim(),
                    adminEmail.trim().toLowerCase(),
                    adminPassword,
                    companyName.trim(),
                    registrationNumber.trim(),
                    companyEmail.trim().toLowerCase(),
                    companyUrl != null ? companyUrl.trim() : null,
                    description != null ? description.trim() : null,
                    industry != null ? industry.trim() : null,
                    location != null ? location.trim() : null,
                    baseUrl
            );

            MainLogger.logServiceOperation(SERVLET_NAME, "COMPANY_REGISTER", true,
                    "Company: " + companyName + ", Admin: " + adminEmail + ", URL Verified: " + company.getUrlVerified());

            // Redirect to login with pending message
            req.getSession().setAttribute("successMessage",
                    "Registration successful! Please check your email to verify your account. " +
                            "Your company registration is pending admin approval.");
            resp.sendRedirect(req.getContextPath() + "/login?registered=true&pendingApproval=true");

        } catch (IllegalArgumentException e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "COMPANY_REGISTER",
                    e.getMessage(), adminEmail != null ? adminEmail : "unknown");
            req.setAttribute("error", e.getMessage());
            preserveFormData(req, adminName, adminEmail, companyName, registrationNumber, companyEmail, companyUrl,
                    description, industry, location);
            req.getRequestDispatcher("/views/company-register.jsp").forward(req, resp);
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "COMPANY_REGISTER",
                    e.getMessage(), adminEmail != null ? adminEmail : "unknown");
            req.setAttribute("error", "Registration failed. Please try again later.");
            preserveFormData(req, adminName, adminEmail, companyName, registrationNumber, companyEmail, companyUrl,
                    description, industry, location);
            req.getRequestDispatcher("/views/company-register.jsp").forward(req, resp);
        }
    }

    private void validateInputs(String adminName, String adminEmail, String adminPassword,
                                String confirmPassword, String companyName,
                                String registrationNumber, String companyEmail) {
        if (adminName == null || adminName.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin name is required");
        }
        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin email is required");
        }
        if (!userService.isValidPassword(adminPassword)) {
            throw new IllegalArgumentException(userService.getPasswordRequirementsMessage());
        }
        if (!adminPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required");
        }
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number is required");
        }
        if (companyEmail == null || companyEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Company email is required");
        }
    }

    private void preserveFormData(HttpServletRequest req, String adminName, String adminEmail,
                                  String companyName, String registrationNumber,
                                  String companyEmail, String companyUrl,
                                  String description, String industry, String location) {
        req.setAttribute("adminName", adminName);
        req.setAttribute("adminEmail", adminEmail);
        req.setAttribute("companyName", companyName);
        req.setAttribute("registrationNumber", registrationNumber);
        req.setAttribute("companyEmail", companyEmail);
        req.setAttribute("companyUrl", companyUrl);
        req.setAttribute("description", description);
        req.setAttribute("industry", industry);
        req.setAttribute("location", location);
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
