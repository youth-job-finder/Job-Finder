package com.jakartaee.jobfinder.dao;

import com.jakartaee.jobfinder.entity.CV;
import com.jakartaee.jobfinder.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class CVDAO {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void saveOrUpdate(CV cv) {
        if (cv.getId() == null) {
            em.persist(cv);
        } else {
            em.merge(cv);
        }
    }

    public Optional<CV> findByUser(User user) {
        try {
            CV cv = em.createQuery(
                            "SELECT c FROM CV c WHERE c.user = :user", CV.class)
                    .setParameter("user", user)
                    .getSingleResult();
            return Optional.of(cv);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    public void delete(CV cv) {
        CV managed = em.find(CV.class, cv.getId());
        if (managed != null) {
            em.remove(managed);
        }
    }
}
