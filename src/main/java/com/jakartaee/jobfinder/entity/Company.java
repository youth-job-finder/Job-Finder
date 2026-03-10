package com.jakartaee.jobfinder.entity;

import com.jakartaee.jobfinder.entity.status.Status;
import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id", nullable = false, unique = true)
    private String id;

    @Column(name = "company_name", length = 100, nullable = false)
    private String name;

    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false)
    private Status status = Status.PENDING;

    // Required no-arg constructor for JPA
    public Company() {}

    // Constructor with all fields (status is passed, not hard-coded)
    public Company(String name, String registrationNumber, String email, String url, Status status) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.url = url;
        this.status = status;
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
}

