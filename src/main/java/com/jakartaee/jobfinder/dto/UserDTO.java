/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jakartaee.jobfinder.dto;

/**
 *
 * @author PilmanGDM
 */

import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;

public class UserDTO {

    private String id;
    private String name;
    private String email;
    private Role role;

    // Empty constructor for JSON mapping
    public UserDTO() {}

    // MAGIC TRICK: A constructor that automatically converts an Entity into a DTO
    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        // Notice we completely ignore the passwordHash! It stays safely behind.
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}