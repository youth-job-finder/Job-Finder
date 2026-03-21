/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jakartaee.jobfinder.dao;

/**
 *
 * @author pilot
 */

import com.jakartaee.jobfinder.entity.Application;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class ApplicationDAO {

    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    public void create(Application application) {
        em.persist(application);
    }

    public Application findById(String id) {
        return em.find(Application.class, id);
    }

    public List<Application> findAll() {
        return em.createQuery("SELECT a FROM Application a", Application.class).getResultList();
    }

    public Application update(Application application) {
        return em.merge(application);
    }

    public void delete(String id) {
        Application application = findById(id);
        if (application != null) {
            em.remove(application);
        }
    }
    
    // Custom: Find all applications made by a specific applicant (User)
    public List<Application> findByApplicantId(String applicantId) {
        TypedQuery<Application> query = em.createQuery(
            "SELECT a FROM Application a WHERE a.applicant.id = :applicantId", Application.class);
        query.setParameter("applicantId", applicantId);
        return query.getResultList();
    }

    // Custom: Find all applications submitted for a specific Job
    public List<Application> findByJobId(String jobId) {
        TypedQuery<Application> query = em.createQuery(
            "SELECT a FROM Application a WHERE a.job.id = :jobId", Application.class);
        query.setParameter("jobId", jobId);
        return query.getResultList();
    }
}