package com.jakartaee.jobfinder.startup;

import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.security.utils.BCryptHashAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initializes default system data on application startup.
 * Creates a default system admin user if one does not already exist.
 */
@Singleton
@Startup
public class DefaultAdminInitializer {

    private static final Logger LOGGER = Logger.getLogger(DefaultAdminInitializer.class.getName());

    private static final String DEFAULT_ADMIN_EMAIL = "admin@jobfinder.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String DEFAULT_ADMIN_NAME = "System Administrator";

    @Inject
    private UserDAO userDAO;

    @Inject
    private BCryptHashAlgorithm passwordHasher;

    /**
     * Creates default admin user on startup if it doesn't exist.
     * This method runs automatically when the server starts.
     */
    @PostConstruct
    public void init() {
        try {
            Optional<User> existingAdmin = userDAO.findByEmail(DEFAULT_ADMIN_EMAIL);

            if (existingAdmin.isEmpty()) {
                createDefaultAdmin();
            } else {
                LOGGER.info("Default admin already exists: " + DEFAULT_ADMIN_EMAIL);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize default admin", e);
        }
    }

    private void createDefaultAdmin() {
        String hashedPassword = passwordHasher.generate(DEFAULT_ADMIN_PASSWORD.toCharArray());

        User admin = new User(DEFAULT_ADMIN_NAME, DEFAULT_ADMIN_EMAIL, hashedPassword, Role.SYSTEM_ADMIN);
        admin.setEmailVerified(true);

        userDAO.create(admin);

        LOGGER.info("Default admin created successfully: " + DEFAULT_ADMIN_EMAIL);
    }
}
