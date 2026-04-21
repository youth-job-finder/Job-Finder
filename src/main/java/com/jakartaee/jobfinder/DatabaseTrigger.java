package com.jakartaee.jobfinder;

import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role; 
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class DatabaseTrigger {

    // This automatically brings in the DAO you just built
    @Inject
    private UserDAO userDAO;

    @PostConstruct
    public void init() {
        System.out.println("Waking up the database and generating tables...");

        try {
            // 1. Testing purpose --- test user object
            //User testUser = new User("Pilot", "pilman@test.com", "Uzumaki123", Role.APPLICANT);

            // 2. Hand it to the bouncer (the DAO) to save it to MySQL
            //userDAO.create(testUser);

            System.out.println("SUCCESS! The test user was officially saved to MySQL.");
            
        } catch (Exception e) {
            System.out.println("Database injection failed: " + e.getMessage());
        }
    }
}