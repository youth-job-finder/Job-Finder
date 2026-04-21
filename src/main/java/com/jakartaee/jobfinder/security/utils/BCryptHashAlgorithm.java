package com.jakartaee.jobfinder.security.utils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.security.enterprise.identitystore.PasswordHash;
import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.Map;

@RequestScoped
public class BCryptHashAlgorithm implements PasswordHash {

    @Override
    public void initialize(Map<String, String> parameters) {
        // Optional: configure cost factor from parameters
    }

    @Override
    public String generate(char[] password) {
        return BCrypt.withDefaults().hashToString(12, password);
    }

    @Override
    public boolean verify(char[] password, String hashedPassword) {
        return BCrypt.verifyer().verify(password, hashedPassword).verified;
    }
}
