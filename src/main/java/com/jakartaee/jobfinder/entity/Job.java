package com.jakartaee.jobfinder.entity;

import jakarta.persistence.*;

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

    @Column(name = "job_description", nullable = false)
    private String jobDescription;

    @Column(name = "physical_address", nullable = false)
    private String physicalAddress;

    @Column(name = "job_requirements", nullable = false)
    private String jobRequirements;

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
}
