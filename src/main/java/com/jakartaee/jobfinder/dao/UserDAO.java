/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author pilot
 */
package com.jakartaee.jobfinder.dao;

import com.jakartaee.jobfinder.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless // Now magic happens ma boys: This steless badboy tells GlassFish to automatically handle all database transactions!
public class UserDAO {

    // Now this injects the database connection we set up in our persistence.xml
    @PersistenceContext(unitName = "youth-job-finder")
    private EntityManager em;

    // --- 1. CREATE ---
    public void create(User user) {
        em.persist(user); // Takes the Java object and turns it into a MySQL INSERT statement
    }

    // --- 2. READ (Find by ID) ---
    public User findById(String id) {
        return em.find(User.class, id); // Automatically does a SELECT * WHERE id = ?
    }

    // --- 3. READ (Find All) ---
    public List<User> findAll() {
        // This is JPQL (Jakarta Persistence Query Language), not raw SQL. 
        // It queries the Java Entity 'User', not the MySQL table 'users'.
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    // --- 4. UPDATE ---
    public User update(User user) {
        return em.merge(user); // Updates existing records
    }

    // --- 5. DELETE ---
    public void delete(String id) {
        User user = findById(id);
        if (user != null) {
            em.remove(user); // Deletes the record
        }
    }
    
    // --- CUSTOM QUERY (Find by Email) ---
    public User findByEmail(String email) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        
        // Return null if no user is found instead of crashing
        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
