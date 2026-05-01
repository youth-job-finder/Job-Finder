/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jakartaee.jobfinder.dto;

/**
 *
 * @author PilmanGDM
 */
import com.jakartaee.jobfinder.models.Application;
import com.jakartaee.jobfinder.models.status.Status;

public class ApplicationDTO {

    private String id;
    private Status applicationStatus;
    
    // FLATTENED RELATIONSHIPS: Safe strings instead of full objects
    private String applicantId;
    private String applicantName;
    
    private String jobId;
    private String jobTitle;

    public ApplicationDTO() {}

    public ApplicationDTO(Application application) {
        this.id = application.getId();
        this.applicationStatus = application.getApplicationStatus();
        
        // Safely extract the User (Applicant) details
        if (application.getApplicant() != null) {
            this.applicantId = application.getApplicant().getId();
            this.applicantName = application.getApplicant().getName();
        }
        
        // Safely extract the Job details
        if (application.getJob() != null) {
            this.jobId = application.getJob().getId();
            this.jobTitle = application.getJob().getJobTitle();
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Status getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(Status applicationStatus) { this.applicationStatus = applicationStatus; }

    public String getApplicantId() { return applicantId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
}