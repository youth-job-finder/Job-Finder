/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt
 * to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java
 * to edit this template
 */

package com.jakartaee.jobfinder.dao;

import com.jakartaee.jobfinder.entity.Application;
import com.jakartaee.jobfinder.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Data Access Object (DAO) for managing Application entities.
 *
 * Provides CRUD operations and custom queries for Application objects
 * using JPA and MySQL as the persistence layer.
 *
 * Original Author: pilot
 * Modified & Documented by: Aubrey
 */
@Stateless
public class ApplicationDAO {

    /**
     * Injected EntityManager configured in persistence.xml.
     * Handles database operations automatically within the persistence context.
     */
    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    // --- 1. CREATE ---
    /**
     * Persists a new Application entity into the database.
     *
     * Runs inside a transactional context. If any error occurs
     * (such as a constraint violation), the transaction will roll back.
     *
     * @param application the Application entity to be persisted
     */
    @Transactional
    public void create(Application application) {
        em.persist(application);
    }

    // --- 2. READ (Find by ID) ---
    /**
     * Finds an Application entity by its unique identifier.
     *
     * @param id the primary key of the Application
     * @return the Application entity if found, otherwise null
     */
    public Application findById(String id) {
        return em.find(Application.class, id);
    }

    // --- 3. READ (Find All) ---
    /**
     * Retrieves all Application entities from the database.
     *
     * @return a list of all Application entities
     */
    public List<Application> findAll() {
        return em.createQuery("SELECT a FROM Application a", Application.class)
                .getResultList();
    }

    // --- 4. UPDATE ---
    /**
     * Updates an existing Application entity.
     *
     * Runs inside a transactional context. If any error occurs,
     * the transaction will roll back.
     *
     * @param application the Application entity with updated fields
     * @return the managed Application entity after merge
     */
    @Transactional
    public Application update(Application application) {
        return em.merge(application);
    }

    // --- 5. DELETE ---
    /**
     * Deletes an Application entity by its unique identifier.
     *
     * @param id the primary key of the Application to delete
     */
    public void delete(String id) {
        Application application = findById(id);
        if (application != null) {
            em.remove(application);
        }
    }

    // --- CUSTOM QUERY (Find by Applicant ID) ---
    /**
     * Finds all Application entities submitted by a specific applicant.
     *
     * @param applicantId the unique identifier of the applicant (User)
     * @return a list of Application entities submitted by the applicant
     */
    public List<Application> findByApplicantId(String applicantId) {
        TypedQuery<Application> query = em.createQuery(
                "SELECT a FROM Application a WHERE a.applicant.id = :applicantId",
                Application.class
        );
        query.setParameter("applicantId", applicantId);
        return query.getResultList();
    }

    // --- CUSTOM QUERY (Find by Job ID) ---
    /**
     * Finds all Application entities submitted for a specific job.
     *
     * @param jobId the unique identifier of the job
     * @return a list of Application entities submitted for the job
     */
    public List<Application> findByJobId(String jobId) {
        TypedQuery<Application> query = em.createQuery(
                "SELECT a FROM Application a WHERE a.job.id = :jobId",
                Application.class
        );
        query.setParameter("jobId", jobId);
        return query.getResultList();
    }

    // --- CUSTOM QUERY (Find by Job Entity) ---
    /**
     * Finds all Application entities submitted for a specific job.
     *
     * @param job the Job entity
     * @return a list of Application entities submitted for the job
     */
    public List<Application> findByJob(com.jakartaee.jobfinder.entity.Job job) {
        TypedQuery<Application> query = em.createQuery(
                "SELECT a FROM Application a WHERE a.job = :job",
                Application.class
        );
        query.setParameter("job", job);
        return query.getResultList();
    }

    // --- CUSTOM QUERY (Find by Applicant Entity) ---
    /**
     * Finds all Application entities submitted by a specific applicant.
     *
     * @param applicant the User entity representing the applicant
     * @return a list of Application entities submitted by the applicant
     */
    public List<Application> findByApplicant(com.jakartaee.jobfinder.entity.User applicant) {
        TypedQuery<Application> query = em.createQuery(
                "SELECT a FROM Application a WHERE a.applicant = :applicant",
                Application.class
        );
        query.setParameter("applicant", applicant);
        return query.getResultList();
    }
}
