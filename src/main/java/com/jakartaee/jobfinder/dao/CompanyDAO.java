/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt
 * to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java
 * to edit this template
 */

package com.jakartaee.jobfinder.dao;

import com.jakartaee.jobfinder.entity.Company;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for managing Company entities.
 *
 * Provides CRUD operations and custom queries for Company objects
 * using JPA and MySQL as the persistence layer.
 *
 * Original Author: pilot
 * Modified & Documented by: Aubrey
 */
@Stateless
public class CompanyDAO {

    /**
     * Injected EntityManager configured in persistence.xml.
     * Handles database operations automatically within the persistence context.
     */
    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    // --- 1. CREATE ---
    /**
     * Persists a new Company entity into the database.
     *
     * Runs inside a transactional context. If any error occurs
     * (such as a constraint violation), the transaction will roll back.
     *
     * @param company the Company entity to be persisted
     */
    @Transactional
    public Company create(Company company) {
        em.persist(company);
        return company;
    }

    // --- 2. READ (Find by ID) ---
    /**
     * Finds a Company entity by its unique identifier.
     *
     * @param id the primary key of the Company
     * @return the Company entity if found, otherwise null
     */
    public Company findById(String id) {
        return em.find(Company.class, id);
    }

    // --- 3. READ (Find All) ---
    /**
     * Retrieves all Company entities from the database.
     *
     * @return a list of all Company entities
     */
    public List<Company> findAll() {
        return em.createQuery("SELECT c FROM Company c", Company.class)
                .getResultList();
    }

    // --- 4. UPDATE ---
    /**
     * Updates an existing Company entity.
     *
     * Runs inside a transactional context. If any error occurs,
     * the transaction will roll back.
     *
     * @param company the Company entity with updated fields
     * @return the managed Company entity after merge
     */
    @Transactional
    public Company update(Company company) {
        return em.merge(company);
    }

    // --- 5. DELETE ---
    /**
     * Deletes a Company entity by its unique identifier.
     *
     * @param id the primary key of the Company to delete
     */
    public void delete(String id) {
        Company company = findById(id);
        if (company != null) {
            em.remove(company);
        }
    }

    // --- CUSTOM QUERY (Find by Registration Number) ---
    /**
     * Finds a Company entity by its unique registration number.
     *
     * @param regNumber the registration number of the Company
     * @return the Company entity if found, otherwise null
     */
    public Company findByRegistrationNumber(String regNumber) {
        TypedQuery<Company> query = em.createQuery(
                "SELECT c FROM Company c WHERE c.registrationNumber = :regNumber",
                Company.class
        );
        query.setParameter("regNumber", regNumber);

        List<Company> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    // --- CUSTOM QUERY (Find by User ID) ---
    /**
     * Finds a Company entity by the company admin's user ID.
     *
     * @param userId the ID of the company admin user
     * @return the Company entity if found, otherwise null
     */
    public Company findByUserId(String userId) {
        TypedQuery<Company> query = em.createQuery(
                "SELECT c FROM Company c WHERE c.companyAdmin.id = :userId",
                Company.class
        );
        query.setParameter("userId", userId);

        List<Company> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    // --- CUSTOM QUERY (Find by User) ---
    /**
     * Finds all Company entities associated with a specific user.
     *
     * @param user the User entity
     * @return a list of Company entities associated with the user
     */
    public List<Company> findByUser(com.jakartaee.jobfinder.entity.User user) {
        TypedQuery<Company> query = em.createQuery(
                "SELECT c FROM Company c WHERE c.companyAdmin.id = :userId",
                Company.class
        );
        query.setParameter("userId", user.getId());
        return query.getResultList();
    }

    // --- CUSTOM QUERY (Find by Status) ---
    /**
     * Finds all Company entities with a specific status.
     *
     * @param status the status to search for (e.g., PENDING, APPROVED, REJECTED)
     * @return a list of Company entities with the specified status
     */
    public List<Company> findByStatus(com.jakartaee.jobfinder.entity.status.Status status) {
        TypedQuery<Company> query = em.createQuery(
                "SELECT c FROM Company c WHERE c.status = :status",
                Company.class
        );
        query.setParameter("status", status);
        return query.getResultList();
    }

    // --- CUSTOM QUERY (Find by Name) ---
    /**
     * Finds a Company entity by its name.
     *
     * @param name the name of the Company
     * @return an Optional containing the Company entity if found, otherwise empty
     */
    public Optional<Company> findByName(String name) {
        TypedQuery<Company> query = em.createQuery(
                "SELECT c FROM Company c WHERE c.name = :name",
                Company.class
        );
        query.setParameter("name", name);

        List<Company> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
