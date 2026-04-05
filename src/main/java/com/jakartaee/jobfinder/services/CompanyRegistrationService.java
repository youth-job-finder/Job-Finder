package com.jakartaee.jobfinder.services;

import com.jakartaee.jobfinder.dao.CompanyDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.Company;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.entity.status.Status;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.security.utils.BCryptHashAlgorithm;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;

@ApplicationScoped
public class CompanyRegistrationService {

    @Inject
    private UserDAO userDAO;

    @Inject
    private CompanyDAO companyDAO;

    @Inject
    private BCryptHashAlgorithm passwordHasher;

    @Inject
    private TokenService tokenService;

    @Inject
    private EmailService emailService;

    /**
     * Registers a company admin user and their company in a single transaction.
     * Flow:
     * 1. Create user with COMPANY_ADMIN role
     * 2. Generate and send email verification token
     * 3. Create company linked to the user
     * 4. Attempt automatic URL verification
     * 5. Set company status to PENDING (awaiting admin approval)
     *
     * @param adminName Company admin full name
     * @param adminEmail Company admin email
     * @param adminPassword Company admin password
     * @param companyName Company name
     * @param registrationNumber Company registration number
     * @param companyEmail Company email
     * @param companyUrl Company website URL
     * @param description Company description
     * @param industry Company industry
     * @param location Company location
     * @param baseUrl Base URL for email links
     * @return The created Company entity
     */
    @Transactional
    public Company registerCompanyWithAdmin(String adminName, String adminEmail, String adminPassword,
                                               String companyName, String registrationNumber,
                                               String companyEmail, String companyUrl, String description,
                                               String industry, String location, String baseUrl) {

        // Validate inputs
        validateInputs(adminName, adminEmail, adminPassword, companyName, registrationNumber, companyEmail);

        // Check if email already exists
        if (userDAO.findByEmail(adminEmail).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + adminEmail);
        }

        // Check if company email already exists
        if (companyDAO.findByRegistrationNumber(registrationNumber) != null) {
            throw new IllegalArgumentException("Registration number already exists: " + registrationNumber);
        }

        // 1. Create company admin user
        String hashedPassword = passwordHasher.generate(adminPassword.toCharArray());
        User companyAdmin = new User(adminName, adminEmail, hashedPassword, Role.COMPANY_ADMIN);

        // Generate email verification token
        String verificationToken = tokenService.generateVerificationToken();
        companyAdmin.setVerificationToken(verificationToken);
        companyAdmin.setVerificationTokenExpiry(tokenService.getVerificationTokenExpiry());
        companyAdmin.setEmailVerified(false);

        User createdAdmin = userDAO.create(companyAdmin);
        MainLogger.logServiceOperation("CompanyRegistrationService", "CREATE_COMPANY_ADMIN",
                true, "Email: " + adminEmail);

        // 2. Send email verification
        boolean emailSent = emailService.sendVerificationEmail(adminEmail, adminName, verificationToken, baseUrl);
        if (emailSent) {
            MainLogger.logServiceOperation("CompanyRegistrationService", "VERIFICATION_EMAIL_SENT",
                    true, "Email: " + adminEmail);
        } else {
            MainLogger.logAuthenticationError("CompanyRegistrationService", "VERIFICATION_EMAIL",
                    "Failed to send verification email", adminEmail);
        }

        // 3. Create company linked to admin
        Company company = new Company();
        company.setCompanyAdmin(createdAdmin);
        company.setName(companyName);
        company.setRegistrationNumber(registrationNumber);
        company.setEmail(companyEmail);
        company.setUrl(companyUrl);
        company.setDescription(description);
        company.setIndustry(industry);
        company.setLocation(location);
        company.setStatus(Status.PENDING); // Awaiting admin approval
        company.setUrlVerified(false);

        // 4. Attempt automatic URL verification
        boolean urlValid = verifyCompanyUrl(companyUrl);
        if (urlValid) {
            company.setUrlVerified(true);
            company.setUrlVerifiedAt(LocalDateTime.now());
            MainLogger.logServiceOperation("CompanyRegistrationService", "URL_VERIFICATION",
                    true, "URL: " + companyUrl);
        } else {
            MainLogger.logAuthenticationError("CompanyRegistrationService", "URL_VERIFICATION",
                    "URL verification failed", companyUrl);
        }

        companyDAO.create(company);
        MainLogger.logServiceOperation("CompanyRegistrationService", "CREATE_COMPANY",
                true, "Company: " + companyName + ", Admin: " + adminEmail);

        // 5. Send notification to system admins about new company registration
        notifySystemAdmins(company, createdAdmin);

        return company;
    }

    /**
     * Verifies a company URL by attempting an HTTP connection.
     * Handles SSL issues, redirects, and various server responses.
     *
     * @param urlString The URL to verify
     * @return true if URL is accessible and returns valid response
     */
    public boolean verifyCompanyUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return false;
        }

        // Ensure URL has protocol
        String normalizedUrl = urlString.trim();
        if (!normalizedUrl.startsWith("http://") && !normalizedUrl.startsWith("https://")) {
            normalizedUrl = "https://" + normalizedUrl;
        }

        MainLogger.logInfo("CompanyRegistrationService", "Starting URL verification for: " + normalizedUrl);

        // Try HTTPS first with standard SSL
        boolean httpsValid = checkUrlSecure(normalizedUrl, false);
        if (httpsValid) {
            return true;
        }

        // If HTTPS fails due to SSL issues, try with lenient SSL (for dev/testing)
        if (normalizedUrl.startsWith("https://")) {
            boolean lenientValid = checkUrlSecure(normalizedUrl, true);
            if (lenientValid) {
                MainLogger.logInfo("CompanyRegistrationService", "URL verified with lenient SSL: " + normalizedUrl);
                return true;
            }
        }

        // Fallback to HTTP if HTTPS completely fails
        if (normalizedUrl.startsWith("https://")) {
            String httpUrl = "http://" + normalizedUrl.substring(8);
            boolean httpValid = checkUrlSecure(httpUrl, false);
            if (httpValid) {
                MainLogger.logInfo("CompanyRegistrationService", "URL verified via HTTP fallback: " + httpUrl);
                return true;
            }
        }

        MainLogger.logAuthenticationError("CompanyRegistrationService", "URL_VERIFICATION",
                "All verification attempts failed", normalizedUrl);
        return false;
    }

    private boolean checkUrlSecure(String urlString, boolean lenientSsl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            
            if (lenientSsl && urlString.startsWith("https://")) {
                // Create lenient SSL context that trusts all certificates
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, getTrustAllCerts(), new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            }
            
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(15000); // 15 seconds
            connection.setReadTimeout(15000); // 15 seconds
            connection.setInstanceFollowRedirects(true);
            connection.setDoInput(true);
            
            int responseCode = connection.getResponseCode();
            boolean success = responseCode >= 200 && responseCode < 400;
            
            if (success) {
                MainLogger.logServiceOperation("CompanyRegistrationService", 
                        lenientSsl ? "URL_VERIFICATION_LENIENT" : "URL_VERIFICATION_HEAD",
                        true, "URL: " + urlString + " (HTTP " + responseCode + ")");
            } else if (responseCode == 405) {
                // Method not allowed, try GET
                return checkUrlWithGet(urlString, lenientSsl);
            }
            return success;

        } catch (SSLHandshakeException e) {
            MainLogger.logInfo("CompanyRegistrationService", "SSL handshake failed for " + urlString + ": " + e.getMessage());
            return false;
        } catch (IOException e) {
            MainLogger.logInfo("CompanyRegistrationService", "Connection failed for " + urlString + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            MainLogger.logInfo("CompanyRegistrationService", "Unexpected error for " + urlString + ": " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private boolean checkUrlWithGet(String urlString, boolean lenientSsl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            
            if (lenientSsl && urlString.startsWith("https://")) {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, getTrustAllCerts(), new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            }
            
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setInstanceFollowRedirects(true);
            connection.setDoInput(true);

            int responseCode = connection.getResponseCode();
            boolean success = responseCode >= 200 && responseCode < 400;
            
            if (success) {
                MainLogger.logServiceOperation("CompanyRegistrationService",
                        lenientSsl ? "URL_VERIFICATION_GET_LENIENT" : "URL_VERIFICATION_GET",
                        true, "URL: " + urlString + " (HTTP " + responseCode + ")");
            }
            return success;

        } catch (Exception e) {
            MainLogger.logInfo("CompanyRegistrationService", "GET request failed for " + urlString + ": " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private TrustManager[] getTrustAllCerts() {
        return new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };
    }

    /**
     * Approve a pending company registration (called by system admin).
     *
     * @param companyId The company ID to approve
     * @param adminId The system admin ID performing the approval
     * @return true if approval successful
     */
    @Transactional
    public boolean approveCompany(String companyId, String adminId, String baseUrl) {
        Company company = companyDAO.findById(companyId);
        if (company == null) {
            throw new IllegalArgumentException("Company not found: " + companyId);
        }

        if (company.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Company is not in pending status");
        }

        User admin = userDAO.findById(adminId);
        if (admin == null || admin.getRole() != Role.SYSTEM_ADMIN) {
            throw new IllegalArgumentException("Unauthorized approval attempt");
        }

        company.setStatus(Status.APPROVED);
        companyDAO.update(company);

        // Notify company admin
        User companyAdmin = company.getCompanyAdmin();
        if (companyAdmin != null) {
            emailService.sendCompanyApprovalEmail(companyAdmin.getEmail(), companyAdmin.getName(), company.getName(), baseUrl);
        }

        // Also notify company email address if different from admin email
        String companyEmail = company.getEmail();
        if (companyEmail != null && !companyEmail.isEmpty() && 
            (companyAdmin == null || !companyEmail.equalsIgnoreCase(companyAdmin.getEmail()))) {
            emailService.sendCompanyApprovalEmail(companyEmail, company.getName(), company.getName(), baseUrl);
        }

        MainLogger.logServiceOperation("CompanyRegistrationService", "APPROVE_COMPANY",
                true, "Company: " + company.getName() + ", Approved by: " + adminId);

        return true;
    }

    /**
     * Reject a pending company registration (called by system admin).
     *
     * @param companyId The company ID to reject
     * @param adminId The system admin ID performing the rejection
     * @param reason The rejection reason
     * @return true if rejection successful
     */
    @Transactional
    public boolean rejectCompany(String companyId, String adminId, String reason) {
        Company company = companyDAO.findById(companyId);
        if (company == null) {
            throw new IllegalArgumentException("Company not found: " + companyId);
        }

        if (company.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Company is not in pending status");
        }

        User admin = userDAO.findById(adminId);
        if (admin == null || admin.getRole() != Role.SYSTEM_ADMIN) {
            throw new IllegalArgumentException("Unauthorized rejection attempt");
        }

        company.setStatus(Status.REJECTED);
        companyDAO.update(company);

        // Notify company admin
        User companyAdmin = company.getCompanyAdmin();
        if (companyAdmin != null) {
            emailService.sendCompanyRejectionEmail(companyAdmin.getEmail(), companyAdmin.getName(),
                    company.getName(), reason);
        }

        MainLogger.logServiceOperation("CompanyRegistrationService", "REJECT_COMPANY",
                true, "Company: " + company.getName() + ", Rejected by: " + adminId);

        return true;
    }

    /**
     * Get all pending companies awaiting admin approval.
     *
     * @return List of pending companies
     */
    public java.util.List<Company> getPendingCompanies() {
        return companyDAO.findByStatus(Status.PENDING);
    }

    private void validateInputs(String adminName, String adminEmail, String adminPassword,
                                String companyName, String registrationNumber, String companyEmail) {
        if (adminName == null || adminName.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin name is required");
        }
        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin email is required");
        }
        if (adminPassword == null || adminPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
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

    private void notifySystemAdmins(Company company, User admin) {
        // This could be implemented to send emails to all system admins
        // For now, we just log it
        MainLogger.logServiceOperation("CompanyRegistrationService", "NOTIFY_ADMINS",
                true, "New company registration pending approval: " + company.getName());
    }
    
    /**
     * Checks if a company with the given email already exists.
     * 
     * @param companyEmail The company email to check
     * @return true if a company with this email exists, false otherwise
     */
    public boolean isCompanyEmailExists(String companyEmail) {
        if (companyEmail == null || companyEmail.trim().isEmpty()) {
            return false;
        }
        // Check if any company has this email
        java.util.List<Company> allCompanies = companyDAO.findAll();
        return allCompanies.stream()
                .anyMatch(company -> companyEmail.equalsIgnoreCase(company.getEmail()));
    }
}
