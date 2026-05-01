/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jakartaee.jobfinder.dto;

/**
 *
 * @author PilmanGDM
 */
import com.jakartaee.jobfinder.models.Review;

public class ReviewDTO {

    private String id;
    private int rating;
    private String comment;
    
    // FLATTENED RELATIONSHIPS
    private String applicantId;
    private String applicantName;
    
    private String companyId;
    private String companyName;

    public ReviewDTO() {}

    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        
        // Safely extract the User (Applicant) details
        if (review.getApplicant() != null) {
            this.applicantId = review.getApplicant().getId();
            this.applicantName = review.getApplicant().getName();
        }
        
        // Safely extract the Company details
        if (review.getCompany() != null) {
            this.companyId = review.getCompany().getId();
            this.companyName = review.getCompany().getName();
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getApplicantId() { return applicantId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}