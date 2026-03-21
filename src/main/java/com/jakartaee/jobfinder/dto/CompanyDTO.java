/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jakartaee.jobfinder.dto;

/**
 *
 * @author PilmanGDM
 */

import com.jakartaee.jobfinder.entity.Company;
import com.jakartaee.jobfinder.entity.status.Status;

public class CompanyDTO {

    private String id;
    private String name;
    private String registrationNumber;
    private String email;
    private String url;
    private Status status;

    public CompanyDTO() {}

    public CompanyDTO(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.registrationNumber = company.getRegistrationNumber();
        this.email = company.getEmail();
        this.url = company.getUrl();
        this.status = company.getStatus();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
