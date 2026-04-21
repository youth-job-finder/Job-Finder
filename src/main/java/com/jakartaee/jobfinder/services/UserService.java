package com.jakartaee.jobfinder.services;

import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.security.utils.BCryptHashAlgorithm;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.Optional;

@ApplicationScoped
public class UserService implements Serializable {

    @Inject
    private UserDAO userDAO;

    @Inject
    private BCryptHashAlgorithm passwordHasher;

    @Inject
    private EmailService emailService;

    @Inject
    private TokenService tokenService;

    public User registerUser(String name, String email, String password, Role role, String baseUrl) {
        try {
            if (userDAO.findByEmail(email).isPresent()) {
                MainLogger.logAuthenticationError("UserService", "REGISTER", "Email already registered", email);
                throw new IllegalArgumentException("Email already registered");
            }

            String hashedPassword = passwordHasher.generate(password.toCharArray());
            User user = new User(name, email, hashedPassword, role);
            
            // Generate verification token
            String verificationToken = tokenService.generateVerificationToken();
            user.setVerificationToken(verificationToken);
            user.setVerificationTokenExpiry(tokenService.getVerificationTokenExpiry());
            user.setEmailVerified(false);
            
            User createdUser = userDAO.create(user);
            
            // Send verification email
            boolean emailSent = emailService.sendVerificationEmail(email, name, verificationToken, baseUrl);
            if (emailSent) {
                MainLogger.logServiceOperation("UserService", "VERIFICATION_EMAIL_SENT", true, "Email: " + email);
            } else {
                MainLogger.logAuthenticationError("UserService", "VERIFICATION_EMAIL", "Failed to send verification email", email);
            }
            
            MainLogger.logServiceOperation("UserService", "REGISTER", true, "Email: " + email + ", Role: " + role);
            
            return createdUser;
        } catch (Exception e) {
            MainLogger.logAuthenticationError("UserService", "REGISTER", e.getMessage(), email);
            throw e;
        }
    }

    public boolean verifyEmail(String token) {
        try {
            Optional<User> userOpt = userDAO.findByVerificationToken(token);
            if (userOpt.isEmpty()) {
                MainLogger.logAuthenticationError("UserService", "VERIFY_EMAIL", "Invalid verification token", token);
                return false;
            }

            User user = userOpt.get();
            
            // Check if token is expired
            if (tokenService.isTokenExpired(user.getVerificationTokenExpiry())) {
                MainLogger.logAuthenticationError("UserService", "VERIFY_EMAIL", "Expired verification token", user.getEmail());
                return false;
            }

            // Check if already verified
            if (user.getEmailVerified()) {
                MainLogger.logServiceOperation("UserService", "VERIFY_EMAIL", true, "Already verified: " + user.getEmail());
                return true;
            }

            // Mark email as verified
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);
            userDAO.update(user);
            
            MainLogger.logServiceOperation("UserService", "VERIFY_EMAIL", true, "Email verified: " + user.getEmail());
            return true;
            
        } catch (Exception e) {
            MainLogger.logAuthenticationError("UserService", "VERIFY_EMAIL", e.getMessage(), token);
            return false;
        }
    }

    public boolean resendVerificationEmail(String email, String baseUrl) {
        try {
            Optional<User> userOpt = userDAO.findByEmail(email);
            if (userOpt.isEmpty()) {
                MainLogger.logAuthenticationError("UserService", "RESEND_VERIFICATION", "User not found", email);
                return false;
            }

            User user = userOpt.get();
            
            // Check if already verified
            if (user.getEmailVerified()) {
                MainLogger.logServiceOperation("UserService", "RESEND_VERIFICATION", true, "Already verified: " + email);
                return true;
            }

            // Generate new verification token
            String verificationToken = tokenService.generateVerificationToken();
            user.setVerificationToken(verificationToken);
            user.setVerificationTokenExpiry(tokenService.getVerificationTokenExpiry());
            userDAO.update(user);

            // Send verification email
            boolean emailSent = emailService.sendVerificationEmail(email, user.getName(), verificationToken, baseUrl);
            if (emailSent) {
                MainLogger.logServiceOperation("UserService", "RESEND_VERIFICATION", true, "Email: " + email);
                return true;
            } else {
                MainLogger.logAuthenticationError("UserService", "RESEND_VERIFICATION", "Failed to send email", email);
                return false;
            }
            
        } catch (Exception e) {
            MainLogger.logAuthenticationError("UserService", "RESEND_VERIFICATION", e.getMessage(), email);
            return false;
        }
    }

    public boolean requestPasswordReset(String email, String baseUrl) {
        try {
            Optional<User> userOpt = userDAO.findByEmail(email);
            if (userOpt.isEmpty()) {
                // Don't reveal if email exists or not for security
                MainLogger.logServiceOperation("UserService", "PASSWORD_RESET_REQUEST", true, "Email not found: " + email);
                return true;
            }

            User user = userOpt.get();
            
            // Generate password reset token
            String resetToken = tokenService.generatePasswordResetToken();
            user.setVerificationToken(resetToken); // Reuse verification token field for reset token
            user.setVerificationTokenExpiry(tokenService.getPasswordResetTokenExpiry());
            userDAO.update(user);

            // Send password reset email
            boolean emailSent = emailService.sendPasswordResetEmail(email, user.getName(), resetToken, baseUrl);
            if (emailSent) {
                MainLogger.logServiceOperation("UserService", "PASSWORD_RESET_EMAIL_SENT", true, "Email: " + email);
                return true;
            } else {
                MainLogger.logAuthenticationError("UserService", "PASSWORD_RESET_EMAIL", "Failed to send email", email);
                return false;
            }
            
        } catch (Exception e) {
            MainLogger.logAuthenticationError("UserService", "PASSWORD_RESET_REQUEST", e.getMessage(), email);
            return false;
        }
    }

    public boolean resetPassword(String token, String newPassword) {
        try {
            Optional<User> userOpt = userDAO.findByVerificationToken(token);
            if (userOpt.isEmpty()) {
                MainLogger.logAuthenticationError("UserService", "RESET_PASSWORD", "Invalid reset token", token);
                return false;
            }

            User user = userOpt.get();
            
            // Check if token is expired
            if (tokenService.isTokenExpired(user.getVerificationTokenExpiry())) {
                MainLogger.logAuthenticationError("UserService", "RESET_PASSWORD", "Expired reset token", user.getEmail());
                return false;
            }

            // Update password
            String hashedPassword = passwordHasher.generate(newPassword.toCharArray());
            user.setPasswordHash(hashedPassword);
            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);
            userDAO.update(user);
            
            MainLogger.logServiceOperation("UserService", "RESET_PASSWORD", true, "Password reset for: " + user.getEmail());
            return true;
            
        } catch (Exception e) {
            MainLogger.logAuthenticationError("UserService", "RESET_PASSWORD", e.getMessage(), token);
            return false;
        }
    }

    public Optional<User> findUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public User updateUser(User user) {
        return userDAO.update(user);
    }

    public void deleteUser(String userId) {
        userDAO.delete(userId);
    }
    
    /**
     * Validates password strength according to security requirements:
     * - At least 8 characters
     * - At least 1 uppercase letter
     * - At least 1 lowercase letter  
     * - At least 1 digit
     * - At least 1 special character
     * 
     * @param password The password to validate
     * @return true if password meets all requirements, false otherwise
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }
        
        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
    
    /**
     * Gets the password requirements error message
     * 
     * @return The standard password requirements message
     */
    public String getPasswordRequirementsMessage() {
        return "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character";
    }
}
