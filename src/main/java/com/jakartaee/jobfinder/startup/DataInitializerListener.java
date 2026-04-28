package com.jakartaee.jobfinder.startup;

import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.transaction.Transactional;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ServletContextListener that runs the data initializer after JPA schema is created.
 * This ensures database tables exist before attempting to insert data.
 */
@WebListener
public class DataInitializerListener implements ServletContextListener {

    private static final String LISTENER_NAME = "DataInitializerListener";

    @Inject
    private CompanyDataInitializer companyDataInitializer;

    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        MainLogger.logInfo(LISTENER_NAME, "Servlet context initialized - checking if schema is ready");

        try {
            // Wait a bit for JPA to finish schema generation
            Thread.sleep(2000);

            // Check if schema exists by trying to query a table
            if (isSchemaReady()) {
                ensureApplicationCvColumns();
                MainLogger.logInfo(LISTENER_NAME, "Database schema is ready - running data initializer");

                // Run the initializer
                companyDataInitializer.init();

                MainLogger.logInfo(LISTENER_NAME, "Data initialization completed");
            } else {
                MainLogger.logError(LISTENER_NAME, "Database schema is not ready - skipping initialization", null);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            MainLogger.logError(LISTENER_NAME, "Interrupted while waiting for schema", e);
        } catch (Exception e) {
            MainLogger.logError(LISTENER_NAME, "Error during initialization", e);
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        MainLogger.logInfo(LISTENER_NAME, "Servlet context destroyed");
    }

    /**
     * Checks if the database schema is ready by attempting to query the users table.
     */
    private boolean isSchemaReady() {
        try {
            // Try to query the users table to see if schema exists
            em.createNativeQuery("SELECT 1 FROM users LIMIT 1").getResultList();
            return true;
        } catch (Exception e) {
            MainLogger.logInfo(LISTENER_NAME, "Schema not ready yet: " + e.getMessage());
            return false;
        }
    }

    private void ensureApplicationCvColumns() {
        try {
            if (!columnExists("applications", "cv_file_name")) {
                em.createNativeQuery("ALTER TABLE applications ADD COLUMN cv_file_name VARCHAR(255) NULL")
                        .executeUpdate();
                MainLogger.logInfo(LISTENER_NAME, "Added applications.cv_file_name column");
            }

            if (!columnExists("applications", "cv_file_path")) {
                em.createNativeQuery("ALTER TABLE applications ADD COLUMN cv_file_path TEXT NULL")
                        .executeUpdate();
                MainLogger.logInfo(LISTENER_NAME, "Added applications.cv_file_path column");
            }
        } catch (Exception e) {
            MainLogger.logError(LISTENER_NAME, "Failed to ensure application CV columns", e);
        }
    }

    private boolean columnExists(String tableName, String columnName) {
        Number count = (Number) em.createNativeQuery(
                        "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?")
                .setParameter(1, tableName)
                .setParameter(2, columnName)
                .getSingleResult();
        return count != null && count.intValue() > 0;
    }
}
