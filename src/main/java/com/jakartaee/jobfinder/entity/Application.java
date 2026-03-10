package com.jakartaee.jobfinder.entity;

import com.jakartaee.jobfinder.entity.status.Status;
import jakarta.persistence.*;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "application_id", nullable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private Status applicationStatus = Status.PENDING;  // default to pending

    // Required no-arg constructor for JPA
    public Application() {}

    // Constructor with applicant and job (status defaults to pending)
    public Application(User applicant, Job job) {
        this.applicant = applicant;
        this.job = job;
        // status is already PENDING via field initialization
    }

    // Constructor allowing custom status (if needed)
    public Application(User applicant, Job job, Status applicationStatus) {
        this.applicant = applicant;
        this.job = job;
        this.applicationStatus = applicationStatus;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    // Typically no setter for auto-generated id, but if needed (e.g., testing), keep it simple.
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

    public Status getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(Status applicationStatus) {
        this.applicationStatus = applicationStatus;
    }
}
