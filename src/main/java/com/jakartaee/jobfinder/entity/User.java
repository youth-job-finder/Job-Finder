package com.jakartaee.jobfinder.entity;

import com.jakartaee.jobfinder.entity.role.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false, unique = true)
    private String id;

    @Column(name = "full_name", length = 50, nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // Required no-arg constructor for JPA
    public User() {}

    // Constructor with all required fields (including role)
    public User(String name, String email, String passwordHash, Role role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Option 2: If you want a default role (e.g., APPLICANT), you can either:
    // - Initialize the field: private Role role = Role.APPLICANT;
    // - Provide a constructor without role and set a default there.
    // Example with default role:
    /*
    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = Role.APPLICANT; // default
    }
    */

    // Getter for ID – typically no setter is provided for auto-generated fields,
    // but if you need one (e.g., for testing), keep it simple (do NOT generate a new UUID).
    public String getId() {
        return id;
    }

    // Optional setter – use with caution; it simply assigns the given value.
    // Usually you would omit this setter.
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}


