/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt
 * to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java
 * to edit this template
 */

package com.jakartaee.jobfinder.dao;

import com.jakartaee.jobfinder.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

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
    public void create(User user) {
        em.persist(user); // Converts the Java object into a MySQL INSERT statement
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
            em.remove(user); // Executes DELETE FROM users WHERE id = ?
        }
    }

    // --- CUSTOM QUERY (Find by Email) ---
    /**
     * Finds a User entity by its email address.
     *
     * @param email the email address to search for
     * @return the User entity if found, otherwise null
     */
    public User findByEmail(String email) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class
        );
        query.setParameter("email", email);

        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
