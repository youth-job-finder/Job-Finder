/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt
 * to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java
 * to edit this template
 */

package com.jakartaee.jobfinder.dao;

import com.jakartaee.jobfinder.models.Review;
import com.jakartaee.jobfinder.models.Company;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Data Access Object (DAO) for managing Review entities.
 *
 * Provides CRUD operations and custom queries for Review objects
 * using JPA and MySQL as the persistence layer.
 *
 * Original Author: pilot
 * Modified & Documented by: Aubrey
 */
@Stateless
public class ReviewDAO {

    /**
     * Injected EntityManager configured in persistence.xml.
     * Handles database operations automatically within the persistence context.
     */
    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    // --- 1. CREATE ---
    /**
     * Persists a new Review entity into the database.
     *
     * Runs inside a transactional context. If any error occurs
     * (such as a constraint violation), the transaction will roll back.
     *
     * @param review the Review entity to be persisted
     */
    @Transactional
    public void create(Review review) {
        em.persist(review);
    }

    // --- 2. READ (Find by ID) ---
    /**
     * Finds a Review entity by its unique identifier.
     *
     * @param id the primary key of the Review
     * @return the Review entity if found, otherwise null
     */
    public Review findById(String id) {
        return em.find(Review.class, id);
    }

    // --- 3. READ (Find All) ---
    /**
     * Retrieves all Review entities from the database.
     *
     * @return a list of all Review entities
     */
    public List<Review> findAll() {
        return em.createQuery("SELECT r FROM Review r", Review.class)
                .getResultList();
    }

    // --- 4. UPDATE ---
    /**
     * Updates an existing Review entity.
     *
     * Runs inside a transactional context. If any error occurs,
     * the transaction will roll back.
     *
     * @param review the Review entity with updated fields
     * @return the managed Review entity after merge
     */
    @Transactional
    public Review update(Review review) {
        return em.merge(review);
    }

    // --- 5. DELETE ---
    /**
     * Deletes a Review entity by its unique identifier.
     *
     * @param id the primary key of the Review to delete
     */
    public void delete(String id) {
        Review review = findById(id);
        if (review != null) {
            em.remove(review);
        }
    }

    // --- CUSTOM QUERY (Find by Company) ---
    /**
     * Finds all Review entities for a specific company.
     *
     * @param company the Company entity
     * @return a list of Review entities for the company
     */
    public List<Review> findByCompany(Company company) {
        TypedQuery<Review> query = em.createQuery(
                "SELECT r FROM Review r WHERE r.company = :company",
                Review.class
        );
        query.setParameter("company", company);
        return query.getResultList();
    }

    // --- CUSTOM QUERY (Find by Company ID) ---
    /**
     * Finds all Review entities for a specific company.
     *
     * @param companyId the unique identifier of the company
     * @return a list of Review entities for the company
     */
    public List<Review> findByCompanyId(String companyId) {
        TypedQuery<Review> query = em.createQuery(
                "SELECT r FROM Review r WHERE r.company.id = :companyId",
                Review.class
        );
        query.setParameter("companyId", companyId);
        return query.getResultList();
    }

    // --- CUSTOM QUERY (Find by Applicant ID) ---
    /**
     * Finds all Review entities written by a specific applicant.
     *
     * @param applicantId the unique identifier of the applicant (User)
     * @return a list of Review entities written by the applicant
     */
    public List<Review> findByApplicantId(String applicantId) {
        TypedQuery<Review> query = em.createQuery(
                "SELECT r FROM Review r WHERE r.applicant.id = :applicantId",
                Review.class
        );
        query.setParameter("applicantId", applicantId);
        return query.getResultList();
    }
}