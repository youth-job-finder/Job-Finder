/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt
 * to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java
 * to edit this template
 */

package com.jakartaee.jobfinder.dao;

import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.security.utils.BCryptHashAlgorithm;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for managing User entities.
 *
 * Provides CRUD operations and custom queries for User objects
 * using JPA and MySQL as the persistence layer.
 *
 * Original Author: pilot
 * Modified & Documented by: Aubrey
 */
@Stateless
public class UserDAO {

    /**
     * Injected EntityManager configured in persistence.xml.
     * Handles database operations automatically within the persistence context.
     */
    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    @Inject
    private BCryptHashAlgorithm passwordHasher;

    // --- 1. CREATE ---
    /**
     * Persists a new User entity into the database.
     *
     * Runs inside a transactional context. If any error occurs
     * (such as a constraint violation), the transaction will roll back.
     *
     * @param user the User entity to be persisted
     */
    @Transactional
    public User create(User user) {
        em.persist(user);
        return user;
    }

    // --- 2. READ (Find by ID) ---
    /**
     * Finds a User entity by its unique identifier.
     *
     * @param id the primary key of the User
     * @return the User entity if found, otherwise null
     */
    public User findById(String id) {
        return em.find(User.class, id); // Executes SELECT * FROM users WHERE id = ?
    }

    // --- 3. READ (Find All) ---
    /**
     * Retrieves all User entities from the database.
     *
     * @return a list of all User entities
     */
    public List<User> findAll() {
        // JPQL query (works on entities, not directly on tables)
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    // --- 4. UPDATE ---
    /**
     * Updates an existing User entity.
     *
     * Runs inside a transactional context. If any error occurs,
     * the transaction will roll back.
     *
     * @param user the User entity with updated fields
     * @return the managed User entity after merge
     */
    @Transactional
    public User update(User user) {
        return em.merge(user); // Converts changes into an SQL UPDATE statement
    }

    // --- 5. DELETE ---
    /**
     * Deletes a User entity by its unique identifier.
     *
     * Runs inside a transactional context. If any error occurs,
     * the transaction will roll back.
     *
     * @param id the primary key of the User to delete
     */
    @Transactional
    public void delete(String id) {
        User user = findById(id);
        if (user != null) {
            // Delete applications for jobs of companies owned by this user
            em.createQuery("DELETE FROM Application a WHERE a.job.id IN " +
                          "(SELECT j.id FROM Job j WHERE j.company.id IN " +
                          "(SELECT c.id FROM Company c WHERE c.companyAdmin.id = :userId))")
              .setParameter("userId", id)
              .executeUpdate();

            // Delete jobs of companies owned by this user
            em.createQuery("DELETE FROM Job j WHERE j.company.id IN " +
                          "(SELECT c.id FROM Company c WHERE c.companyAdmin.id = :userId)")
              .setParameter("userId", id)
              .executeUpdate();

            // Delete companies where user is company admin
            em.createQuery("DELETE FROM Company c WHERE c.companyAdmin.id = :userId")
              .setParameter("userId", id)
              .executeUpdate();

            // Delete applications where user is the applicant
            em.createQuery("DELETE FROM Application a WHERE a.applicant.id = :userId")
              .setParameter("userId", id)
              .executeUpdate();

            em.remove(user); // Executes DELETE FROM users WHERE id = ?
        }
    }

    // --- CUSTOM QUERY (Find by Email) ---
    /**
     * Finds a User entity by its email address.
     *
     * @param email the email address to search for
     * @return the User entity if found, otherwise Optional.empty()
     */
    public Optional<User> findByEmail(String email) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class
        );
        query.setParameter("email", email);

        List<User> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // --- CUSTOM QUERY (Find by Verification Token) ---
    /**
     * Finds a User entity by its verification token.
     *
     * @param token the verification token to search for
     * @return the User entity if found, otherwise Optional.empty()
     */
    public Optional<User> findByVerificationToken(String token) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.verificationToken = :token", User.class
        );
        query.setParameter("token", token);

        List<User> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // --- PASSWORD METHODS ---
    /**
     * Updates a user's password.
     *
     * @param id          the user ID
     * @param newPassword the new password (already hashed)
     */
    @Transactional
    public void updatePassword(String id, String newPassword) {
        User user = findById(id);
        if (user != null) {
            String hashedPassword = passwordHasher.generate(newPassword.toCharArray());
            user.setPasswordHash(hashedPassword);
            em.merge(user);
        }
    }

    /**
     * Verifies a user's password using BCrypt.
     *
     * @param id       the user ID
     * @param password the password to verify
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String id, String password) {
        User user = findById(id);
        if (user == null || user.getPasswordHash() == null) {
            return false;
        }
        return passwordHasher.verify(password.toCharArray(), user.getPasswordHash());
    }
}
