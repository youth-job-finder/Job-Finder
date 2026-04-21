package com.jakartaee.jobfinder.entity;

import com.jakartaee.jobfinder.entity.status.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id", nullable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User companyAdmin;

    @Column(name = "company_name", length = 100, nullable = false)
    private String name;

    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "location", length = 200)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "url_verified", nullable = false)
    private Boolean urlVerified = false;

    @Column(name = "url_verified_at")
    private LocalDateTime urlVerifiedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    // Required no-arg constructor for JPA
    public Company() {}

    // Constructor with User (company admin)
    public Company(User companyAdmin, String name, String registrationNumber, String email, String url, Status status) {
        this.companyAdmin = companyAdmin;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.url = url;
        this.status = status;
        this.created_at = LocalDateTime.now();
    }

    // Constructor with all fields (status is passed, not hard-coded)
    public Company(String name, String registrationNumber, String email, String url, Status status) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.url = url;
        this.status = status;
        this.created_at = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Getter for ID – no setter is typically provided for auto-generated fields,
    // but if you need one (e.g., for testing), use a simple setter (do not generate a new UUID).
    public String getId() {
        return id;
    }

    // Optional setter – use with caution; it does NOT generate a new UUID.
    // It simply assigns the given value. Usually you would omit this setter.
    public void setId(String id) {
        this.id = id;
    }

    public User getCompanyAdmin() {
        return companyAdmin;
    }

    public void setCompanyAdmin(User companyAdmin) {
        this.companyAdmin = companyAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getUrlVerified() {
        return urlVerified;
    }

    public void setUrlVerified(Boolean urlVerified) {
        this.urlVerified = urlVerified;
    }

    public LocalDateTime getUrlVerifiedAt() {
        return urlVerifiedAt;
    }

    public void setUrlVerifiedAt(LocalDateTime urlVerifiedAt) {
        this.urlVerifiedAt = urlVerifiedAt;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    // Alias method for consistency
    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    // Alias method for consistency with User entity
    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (created_at == null) {
            created_at = LocalDateTime.now();
        }
        updated_at = LocalDateTime.now();
    }
}

