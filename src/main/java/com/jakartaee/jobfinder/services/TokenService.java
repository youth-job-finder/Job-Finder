package com.jakartaee.jobfinder.services;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@ApplicationScoped
public class TokenService implements Serializable {

    private static final SecureRandom secureRandom = new SecureRandom();

    public String generateVerificationToken() {
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    public String generatePasswordResetToken() {
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    public boolean isTokenExpired(LocalDateTime expiryTime) {
        return expiryTime == null || LocalDateTime.now().isAfter(expiryTime);
    }

    public LocalDateTime getVerificationTokenExpiry() {
        return LocalDateTime.now().plusHours(2); // 2 hours expiry
    }

    public LocalDateTime getPasswordResetTokenExpiry() {
        return LocalDateTime.now().plusHours(1); // 1 hour expiry
    }
}
