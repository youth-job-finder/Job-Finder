/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jakartaee.jobfinder.dao;

/**
 *
 * @author pilot
 */ 

import com.jakartaee.jobfinder.entity.Job;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class JobDAO {

    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    public void create(Job job) {
        em.persist(job);
    }

    public Job findById(String id) {
        return em.find(Job.class, id);
    }

    public List<Job> findAll() {
        return em.createQuery("SELECT j FROM Job j", Job.class).getResultList();
    }

    public Job update(Job job) {
        return em.merge(job);
    }

    public void delete(String id) {
        Job job = findById(id);
        if (job != null) {
            em.remove(job);
        }
    }
    
    // Custom query: Find all jobs posted by a specific company
    public List<Job> findByCompanyId(String companyId) {
        TypedQuery<Job> query = em.createQuery(
            "SELECT j FROM Job j WHERE j.company.id = :companyId", Job.class);
        query.setParameter("companyId", companyId);
        return query.getResultList();
    }
}