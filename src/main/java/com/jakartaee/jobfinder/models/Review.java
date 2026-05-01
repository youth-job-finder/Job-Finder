package com.jakartaee.jobfinder.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id", nullable = false, unique = true)
    private String id;

    // Many reviews belong to one applicant (User)
    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    // Many reviews belong to one company
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "rating", nullable = false)
    private int rating;          // e.g., 1–5; validation can be added at service layer

    @Column(name = "comment", length = 500)   // optional, with max length
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    // Required no-arg constructor for JPA
    public Review() {}

    // Convenience constructor for required fields
    public Review(User applicant, Company company, int rating) {
        this.applicant = applicant;
        this.company = company;
        this.rating = rating;
    }

    // Constructor with all fields
    public Review(User applicant, Company company, int rating, String comment) {
        this.applicant = applicant;
        this.company = company;
        this.rating = rating;
        this.comment = comment;
        this.created_at = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    // Typically no setter for auto-generated id; if needed (e.g., testing), keep simple.
    public void setId(String id) {
        this.id = id;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = LocalDateTime.now();
    }
}
