package com.jakartaee.jobfinder.entity;

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

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "job_id", nullable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false) // foreign key column
    private Company company;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "job_description", nullable = false, columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "physical_address", nullable = false)
    private String physicalAddress;

    @Column(name = "job_requirements", nullable = false, columnDefinition = "TEXT")
    private String jobRequirements;

    @Column(name = "salary_range", length = 100)
    private String salaryRange;

    @Column(name = "job_type", length = 50)
    private String jobType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    // Required no-arg constructor for JPA
    public Job() {}

    // Constructor with all required fields (except auto-generated id)
    public Job(Company company, String jobTitle, String jobDescription,
               String physicalAddress, String jobRequirements) {
        this.company = company;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.physicalAddress = physicalAddress;
        this.jobRequirements = jobRequirements;
        this.created_at = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    // Typically no setter for auto-generated id, but if needed (e.g., for testing), add a simple setter.
    public void setId(String id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getJobRequirements() {
        return jobRequirements;
    }

    public void setJobRequirements(String jobRequirements) {
        this.jobRequirements = jobRequirements;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    // Alias for JSP EL compatibility (createdAt -> getCreatedAt)
    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    @PrePersist
    public void prePersist() {
        if (created_at == null) {
            created_at = LocalDateTime.now();
        }
    }
}
