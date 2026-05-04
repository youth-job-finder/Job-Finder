package com.jakartaee.jobfinder.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

/**
 * Entity representing a saved/bookmarked job by an applicant.
 * Tracks which jobs users have saved for later viewing.
 */
@Entity
@Table(name = "saved_jobs")
public class SavedJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "saved_job_id", nullable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    // Required no-arg constructor for JPA
    public SavedJob() {}

    // Constructor with applicant and job
    public SavedJob(User applicant, Job job) {
        this.applicant = applicant;
        this.job = job;
        this.created_at = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    // Alias for JSP EL compatibility
    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    @PrePersist
    public void prePersist() {
        if (created_at == null) {
            created_at = LocalDateTime.now();
        }
    }
}
