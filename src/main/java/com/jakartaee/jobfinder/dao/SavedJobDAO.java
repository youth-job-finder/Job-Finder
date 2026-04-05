package com.jakartaee.jobfinder.dao;

import com.jakartaee.jobfinder.entity.SavedJob;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.Job;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for managing SavedJob entities.
 *
 * Provides CRUD operations and custom queries for saved/bookmarked jobs
 * using JPA and MySQL as the persistence layer.
 */
@Stateless
public class SavedJobDAO {

    /**
     * Injected EntityManager configured in persistence.xml.
     * Handles database operations automatically within the persistence context.
     */
    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    // --- 1. CREATE ---
    /**
     * Persists a new SavedJob entity into the database.
     *
     * @param savedJob the SavedJob entity to be persisted
     */
    @Transactional
    public SavedJob create(SavedJob savedJob) {
        em.persist(savedJob);
        return savedJob;
    }

    // --- 2. READ (Find by ID) ---
    /**
     * Finds a SavedJob entity by its unique identifier.
     *
     * @param id the primary key of the SavedJob
     * @return the SavedJob entity if found, otherwise null
     */
    public SavedJob findById(String id) {
        return em.find(SavedJob.class, id);
    }

    // --- 3. READ (Find All) ---
    /**
     * Retrieves all SavedJob entities from the database.
     *
     * @return a list of all SavedJob entities
     */
    public List<SavedJob> findAll() {
        return em.createQuery("SELECT s FROM SavedJob s", SavedJob.class)
                .getResultList();
    }

    // --- 4. UPDATE ---
    /**
     * Updates an existing SavedJob entity.
     *
     * @param savedJob the SavedJob entity with updated fields
     * @return the managed SavedJob entity after merge
     */
    @Transactional
    public SavedJob update(SavedJob savedJob) {
        return em.merge(savedJob);
    }

    // --- 5. DELETE ---
    /**
     * Deletes a SavedJob entity by its unique identifier.
     *
     * @param id the primary key of the SavedJob to delete
     */
    @Transactional
    public void delete(String id) {
        SavedJob savedJob = findById(id);
        if (savedJob != null) {
            em.remove(savedJob);
        }
    }

    /**
     * Deletes a SavedJob by applicant and job.
     *
     * @param applicant the applicant user
     * @param job the job entity
     */
    @Transactional
    public void deleteByApplicantAndJob(User applicant, Job job) {
        TypedQuery<SavedJob> query = em.createQuery(
                "SELECT s FROM SavedJob s WHERE s.applicant = :applicant AND s.job = :job",
                SavedJob.class
        );
        query.setParameter("applicant", applicant);
        query.setParameter("job", job);
        List<SavedJob> results = query.getResultList();
        for (SavedJob savedJob : results) {
            em.remove(savedJob);
        }
    }

    // --- CUSTOM QUERIES ---

    /**
     * Finds all SavedJob entities for a specific applicant.
     *
     * @param applicantId the unique identifier of the applicant (User)
     * @return a list of SavedJob entities for the applicant
     */
    public List<SavedJob> findByApplicantId(String applicantId) {
        TypedQuery<SavedJob> query = em.createQuery(
                "SELECT s FROM SavedJob s WHERE s.applicant.id = :applicantId ORDER BY s.created_at DESC",
                SavedJob.class
        );
        query.setParameter("applicantId", applicantId);
        return query.getResultList();
    }

    /**
     * Finds all SavedJob entities for a specific applicant (by User entity).
     *
     * @param applicant the User entity representing the applicant
     * @return a list of SavedJob entities for the applicant
     */
    public List<SavedJob> findByApplicant(User applicant) {
        TypedQuery<SavedJob> query = em.createQuery(
                "SELECT s FROM SavedJob s WHERE s.applicant = :applicant ORDER BY s.created_at DESC",
                SavedJob.class
        );
        query.setParameter("applicant", applicant);
        return query.getResultList();
    }

    /**
     * Finds all SavedJob entities for a specific job.
     *
     * @param jobId the unique identifier of the job
     * @return a list of SavedJob entities for the job
     */
    public List<SavedJob> findByJobId(String jobId) {
        TypedQuery<SavedJob> query = em.createQuery(
                "SELECT s FROM SavedJob s WHERE s.job.id = :jobId",
                SavedJob.class
        );
        query.setParameter("jobId", jobId);
        return query.getResultList();
    }

    /**
     * Checks if a job is saved by a specific applicant.
     *
     * @param applicant the User entity
     * @param job the Job entity
     * @return true if the job is saved by the applicant
     */
    public boolean isJobSavedByApplicant(User applicant, Job job) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(s) FROM SavedJob s WHERE s.applicant = :applicant AND s.job = :job",
                Long.class
        );
        query.setParameter("applicant", applicant);
        query.setParameter("job", job);
        return query.getSingleResult() > 0;
    }

    /**
     * Finds a SavedJob by applicant and job.
     *
     * @param applicant the User entity
     * @param job the Job entity
     * @return Optional containing the SavedJob if found
     */
    public Optional<SavedJob> findByApplicantAndJob(User applicant, Job job) {
        TypedQuery<SavedJob> query = em.createQuery(
                "SELECT s FROM SavedJob s WHERE s.applicant = :applicant AND s.job = :job",
                SavedJob.class
        );
        query.setParameter("applicant", applicant);
        query.setParameter("job", job);
        List<SavedJob> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Counts the number of saved jobs for a specific applicant.
     *
     * @param applicantId the unique identifier of the applicant
     * @return the count of saved jobs
     */
    public long countByApplicantId(String applicantId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(s) FROM SavedJob s WHERE s.applicant.id = :applicantId",
                Long.class
        );
        query.setParameter("applicantId", applicantId);
        return query.getSingleResult();
    }
}
