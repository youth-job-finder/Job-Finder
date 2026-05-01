/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jakartaee.jobfinder.dto;

/**
 *
 * @author PilmanGDM
 */
import com.jakartaee.jobfinder.models.Job;

public class JobDTO {

    private String id;
    private String jobTitle;
    private String jobDescription;
    private String physicalAddress;
    private String jobRequirements;
    
    // FLATTENED RELATIONSHIP: We only take the safe strings, not the whole Company object!
    private String companyId;
    private String companyName;

    public JobDTO() {}

    public JobDTO(Job job) {
        this.id = job.getId();
        this.jobTitle = job.getJobTitle();
        this.jobDescription = job.getJobDescription();
        this.physicalAddress = job.getPhysicalAddress();
        this.jobRequirements = job.getJobRequirements();
        
        // Safely extract just the details we need from the attached Company
        if (job.getCompany() != null) {
            this.companyId = job.getCompany().getId();
            this.companyName = job.getCompany().getName();
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public String getPhysicalAddress() { return physicalAddress; }
    public void setPhysicalAddress(String physicalAddress) { this.physicalAddress = physicalAddress; }

    public String getJobRequirements() { return jobRequirements; }
    public void setJobRequirements(String jobRequirements) { this.jobRequirements = jobRequirements; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}