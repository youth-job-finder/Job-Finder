/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jakartaee.jobfinder.dao;

/**
 *
 * @author pilot
 */

import com.jakartaee.jobfinder.entity.Review;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class ReviewDAO {

    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    public void create(Review review) {
        em.persist(review);
    }

    public Review findById(String id) {
        return em.find(Review.class, id);
    }

    public List<Review> findAll() {
        return em.createQuery("SELECT r FROM Review r", Review.class).getResultList();
    }

    public Review update(Review review) {
        return em.merge(review);
    }

    public void delete(String id) {
        Review review = findById(id);
        if (review != null) {
            em.remove(review);
        }
    }

    // Custom: Find all reviews for a specific Company
    public List<Review> findByCompanyId(String companyId) {
        TypedQuery<Review> query = em.createQuery(
            "SELECT r FROM Review r WHERE r.company.id = :companyId", Review.class);
        query.setParameter("companyId", companyId);
        return query.getResultList();
    }

    // Custom: Find all reviews written by a specific User
    public List<Review> findByApplicantId(String applicantId) {
        TypedQuery<Review> query = em.createQuery(
            "SELECT r FROM Review r WHERE r.applicant.id = :applicantId", Review.class);
        query.setParameter("applicantId", applicantId);
        return query.getResultList();
    }
}