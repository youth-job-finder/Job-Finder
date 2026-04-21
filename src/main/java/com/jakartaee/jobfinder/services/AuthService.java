package com.jakartaee.jobfinder.services;

import com.jakartaee.jobfinder.dao.CompanyDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.Company;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Service for authentication and authorization operations.
 * Provides centralized methods for user authentication, role-based access control,
 * and email verification checks across the application.
 */
@ApplicationScoped
public class AuthService {

    @Inject
    private IdentityStore identityStore;

    @Inject
    private UserDAO userDAO;

    @Inject
    private CompanyDAO companyDAO;

    /**
     * Authenticate user from HTTP session.
     * Returns User if authenticated, null if not.
     */
    public User getAuthenticatedUser(HttpServletRequest req) {
        String userId = (String) req.getSession().getAttribute("userId");
        if (userId == null && req.getUserPrincipal() != null) {
            userId = req.getUserPrincipal().getName();
        }
        if (userId == null) {
            return null;
        }
        return userDAO.findById(userId);
    }

    /**
     * Check if user is authenticated. Sends 401 if not.
     * Returns User if authenticated, null if response was sent.
     */
    public User requireAuthentication(HttpServletRequest req, HttpServletResponse resp, String servletName) throws IOException {
        User user = getAuthenticatedUser(req);
        if (user == null) {
            MainLogger.logError(servletName, "Unauthorized attempt - authentication required");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return null;
        }
        return user;
    }

    /**
     * Require APPLICANT role. Sends 403 if not authorized.
     * Returns User if authorized, null if response was sent.
     */
    public User requireApplicant(HttpServletRequest req, HttpServletResponse resp, String servletName) throws IOException {
        User user = requireAuthentication(req, resp, servletName);
        if (user == null) {
            return null;
        }
        if (user.getRole() != Role.APPLICANT) {
            MainLogger.logError(servletName, "Non-applicant user attempted access: " + user.getId());
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Applicant access required");
            return null;
        }
        if (!user.getEmailVerified()) {
            MainLogger.logError(servletName, "Unverified user attempted access: " + user.getId());
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Email verification required. Please verify your email.");
            return null;
        }
        return user;
    }

    /**
     * Require COMPANY_ADMIN role. Sends 403 if not authorized.
     * Returns User if authorized, null if response was sent.
     */
    public User requireCompanyAdmin(HttpServletRequest req, HttpServletResponse resp, String servletName) throws IOException {
        User user = requireAuthentication(req, resp, servletName);
        if (user == null) {
            return null;
        }
        if (user.getRole() != Role.COMPANY_ADMIN) {
            MainLogger.logError(servletName, "Non-company-admin user attempted access: " + user.getId());
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Company Admin access required");
            return null;
        }
        if (!user.getEmailVerified()) {
            MainLogger.logError(servletName, "Unverified company admin attempted access: " + user.getId());
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Email verification required. Please verify your email.");
            return null;
        }
        return user;
    }

    /**
     * Require SYSTEM_ADMIN role. Sends 403 if not authorized.
     * Returns User if authorized, null if response was sent.
     */
    public User requireSystemAdmin(HttpServletRequest req, HttpServletResponse resp, String servletName) throws IOException {
        User user = requireAuthentication(req, resp, servletName);
        if (user == null) {
            return null;
        }
        if (user.getRole() != Role.SYSTEM_ADMIN) {
            MainLogger.logError(servletName, "Non-system-admin user attempted access: " + user.getId());
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "System Admin access required");
            return null;
        }
        return user;
    }

    /**
     * Authenticates a user with email and password credentials.
     *
     * @param email    the user's email address
     * @param password the user's password
     * @return AuthResult containing the authentication outcome
     */
    public AuthResult authenticate(String email, String password) {
        UsernamePasswordCredential credential = new UsernamePasswordCredential(email, password);
        CredentialValidationResult result = identityStore.validate(credential);

        if (result.getStatus() != CredentialValidationResult.Status.VALID) {
            return AuthResult.failure("Invalid credentials");
        }

        String userId = result.getCallerPrincipal().getName();
        User user = userDAO.findByEmail(userId).orElse(null);

        if (user == null) {
            return AuthResult.failure("User not found");
        }

        if (!user.getEmailVerified()) {
            return AuthResult.unverified(user);
        }

        Role role = extractRole(result);

        return AuthResult.success(user, role);
    }

    /**
     * Logs out a user and records the logout event.
     *
     * @param userId the ID of the user logging out
     */
    public void logout(String userId) {
        MainLogger.logLogout(userId, userId, "unknown");
    }

    /**
     * Builds session data for an authenticated user.
     * Populates role-specific information (user details for applicants,
     * company details for company admins).
     *
     * @param user the authenticated user
     * @param role the user's role
     * @return UserSessionData containing session attributes
     */
    public UserSessionData getUserSessionData(User user, Role role) {
        UserSessionData data = new UserSessionData();
        data.setUserId(user.getId());
        data.setRole(role);

        if (role == Role.APPLICANT) {
            data.setUserName(user.getName());
            data.setUserEmail(user.getEmail());
            data.setEmailVerified(user.getEmailVerified());
        } else if (role == Role.COMPANY_ADMIN) {
            List<Company> companies = companyDAO.findByUser(user);
            if (!companies.isEmpty()) {
                Company company = companies.get(0);
                data.setCompanyId(company.getId());
                data.setCompanyName(company.getName());
                data.setCompanyEmail(company.getEmail());
                data.setRegistrationNumber(company.getRegistrationNumber());
            }
        }

        return data;
    }

    /**
     * Extracts the role from the credential validation result.
     * Maps security groups to application roles.
     *
     * @param result the credential validation result from identity store
     * @return the user's Role, or null if no matching role found
     */
    private Role extractRole(CredentialValidationResult result) {
        if (result.getCallerGroups().contains("SYSTEM_ADMIN")) {
            return Role.SYSTEM_ADMIN;
        } else if (result.getCallerGroups().contains("COMPANY_ADMIN")) {
            return Role.COMPANY_ADMIN;
        } else if (result.getCallerGroups().contains("APPLICANT")) {
            return Role.APPLICANT;
        }
        return null;
    }

    /**
     * Gets the redirect URL for a user based on their role.
     *
     * @param role the user's role
     * @return the URL to redirect to after login
     */
    public String getRedirectUrl(Role role) {
        if (role == null) {
            return "/views/login.jsp";
        }
        return switch (role) {
            case SYSTEM_ADMIN -> "/admin/dashboard";
            case COMPANY_ADMIN -> "/company/dashboard";
            case APPLICANT -> "/applicant/dashboard";
        };
    }

    /**
     * Result of an authentication attempt.
     * Contains the user, role, success status, and any error message.
     */
    public static class AuthResult {
        private final boolean success;
        private final boolean unverified;
        private final String message;
        private final User user;
        private final Role role;

        private AuthResult(boolean success, boolean unverified, String message, User user, Role role) {
            this.success = success;
            this.unverified = unverified;
            this.message = message;
            this.user = user;
            this.role = role;
        }

        public static AuthResult success(User user, Role role) {
            return new AuthResult(true, false, null, user, role);
        }

        public static AuthResult failure(String message) {
            return new AuthResult(false, false, message, null, null);
        }

        public static AuthResult unverified(User user) {
            return new AuthResult(false, true, "Please verify your email", user, null);
        }

        public boolean isSuccess() { return success; }
        public boolean isUnverified() { return unverified; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
        public Role getRole() { return role; }
    }

    /**
     * Data holder for user session attributes.
     * Contains user information and company details (for company admins).
     */
    public static class UserSessionData {
        private String userId;
        private Role role;
        private String userName;
        private String userEmail;
        private Boolean emailVerified;
        private String companyId;
        private String companyName;
        private String companyEmail;
        private String registrationNumber;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
        public Boolean getEmailVerified() { return emailVerified; }
        public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
        public String getCompanyId() { return companyId; }
        public void setCompanyId(String companyId) { this.companyId = companyId; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getCompanyEmail() { return companyEmail; }
        public void setCompanyEmail(String companyEmail) { this.companyEmail = companyEmail; }
        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    }
}
