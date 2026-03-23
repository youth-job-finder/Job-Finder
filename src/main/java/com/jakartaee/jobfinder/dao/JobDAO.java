/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt
 * to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java
 * to edit this template
 */

package com.jakartaee.jobfinder.dao;

import com.jakartaee.jobfinder.entity.Job;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Data Access Object (DAO) for managing Job entities.
 *
 * Provides CRUD operations and custom queries for Job objects
 * using JPA and MySQL as the persistence layer.
 *
 * Original Author: pilot
 * Modified & Documented by: Aubrey
 */
@Stateless
public class JobDAO {

    /**
     * Injected EntityManager configured in persistence.xml.
     * Handles database operations automatically within the persistence context.
     */
    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    // --- 1. CREATE ---
    /**
     * Persists a new Job entity into the database.
     *
     * Runs inside a transactional context. If any error occurs
     * (such as a constraint violation), the transaction will roll back.
     *
     * @param job the Job entity to be persisted
     */
    @Transactional
    public void create(Job job) {
        em.persist(job);
    }

    // --- 2. READ (Find by ID) ---
    /**
     * Finds a Job entity by its unique identifier.
     *
     * @param id the primary key of the Job
     * @return the Job entity if found, otherwise null
     */
    public Job findById(String id) {
        return em.find(Job.class, id);
    }

    // --- 3. READ (Find All) ---
    /**
     * Retrieves all Job entities from the database.
     *
     * @return a list of all Job entities
     */
    public List<Job> findAll() {
        return em.createQuery("SELECT j FROM Job j", Job.class)
                .getResultList();
    }

    // --- 4. UPDATE ---
    /**
     * Updates an existing Job entity.
     *
     * Runs inside a transactional context. If any error occurs,
     * the transaction will roll back.
     *
     * @param job the Job entity with updated fields
     * @return the managed Job entity after merge
     */
    @Transactional
    public Job update(Job job) {
        return em.merge(job);
    }

    // --- 5. DELETE ---
    /**
     * Deletes a Job entity by its unique identifier.
     *
     * @param id the primary key of the Job to delete
     */
    public void delete(String id) {
        Job job = findById(id);
        if (job != null) {
            em.remove(job);
        }
    }

    // --- CUSTOM QUERY (Find by Company ID) ---
    /**
     * Finds all Job entities posted by a specific company.
     *
     * @param companyId the unique identifier of the company
     * @return a list of Job entities posted by the company
     */
    public List<Job> findByCompanyId(String companyId) {
        TypedQuery<Job> query = em.createQuery(
                "SELECT j FROM Job j WHERE j.company.id = :companyId",
                Job.class
        );
        query.setParameter("companyId", companyId);
        return query.getResultList();
    }
}