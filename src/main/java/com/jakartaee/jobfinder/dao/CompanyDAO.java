/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jakartaee.jobfinder.dao;

/**
 *
 * @author pilot
 */

import com.jakartaee.jobfinder.entity.Company;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class CompanyDAO {

    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    public void create(Company company) {
        em.persist(company);
    }

    public Company findById(String id) {
        return em.find(Company.class, id);
    }

    public List<Company> findAll() {
        return em.createQuery("SELECT c FROM Company c", Company.class).getResultList();
    }

    public Company update(Company company) {
        return em.merge(company);
    }

    public void delete(String id) {
        Company company = findById(id);
        if (company != null) {
            em.remove(company);
        }
    }
    
    // Custom query: Find a company by its unique registration number
    public Company findByRegistrationNumber(String regNumber) {
        TypedQuery<Company> query = em.createQuery(
            "SELECT c FROM Company c WHERE c.registrationNumber = :regNumber", Company.class);
        query.setParameter("regNumber", regNumber);
        
        List<Company> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}