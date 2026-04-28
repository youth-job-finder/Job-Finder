package com.jakartaee.jobfinder.services;

import com.jakartaee.jobfinder.dao.ApplicationDAO;
import com.jakartaee.jobfinder.dao.CompanyDAO;
import com.jakartaee.jobfinder.dao.JobDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.Application;
import com.jakartaee.jobfinder.entity.Company;
import com.jakartaee.jobfinder.entity.Job;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.status.Status;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ApplicationService {

    @Inject
    private ApplicationDAO applicationDAO;

    @Inject
    private JobDAO jobDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private EmailService emailService;

    @Inject
    private CompanyDAO companyDAO;


    public List<Application> getApplicationsByJob(Job job) {
        return applicationDAO.findByJob(job);
    }

    public List<Application> getApplicationsByJobId(String jobId) {
        Job job = jobDAO.findById(jobId);
        if (job == null) {
            return List.of();
        }
        return applicationDAO.findByJob(job);
    }

    public List<Application> getApplicationsByApplicant(User applicant) {
        return applicationDAO.findByApplicant(applicant);
    }

    public List<Application> getAllApplicationsForCompany(User companyUser) {
        List<Company> companies = companyDAO.findByUser(companyUser);
        if (companies.isEmpty()) {
            return List.of();
        }

        Company company = companies.get(0);
        List<Job> companyJobs = jobDAO.findByCompany(company);
        List<Application> allApplications = new ArrayList<>();
        for (Job job : companyJobs) {
            allApplications.addAll(applicationDAO.findByJob(job));
        }
        return allApplications;
    }

    public Map<String, Integer> getApplicationStatistics(User companyUser) {
        Map<String, Integer> stats = new HashMap<>();
        try {
            List<Application> allApplications = getAllApplicationsForCompany(companyUser);

            stats.put("totalApplications", allApplications.size());

            long pendingCount = allApplications.stream()
                .filter(app -> app.getApplicationStatus() == Status.PENDING)
                .count();
            long approvedCount = allApplications.stream()
                .filter(app -> app.getApplicationStatus() == Status.APPROVED)
                .count();
            long rejectedCount = allApplications.stream()
                .filter(app -> app.getApplicationStatus() == Status.REJECTED)
                .count();

            stats.put("pendingApplications", (int) pendingCount);
            stats.put("approvedApplications", (int) approvedCount);
            stats.put("rejectedApplications", (int) rejectedCount);
        } catch (Exception e) {
            stats.put("totalApplications", 0);
            stats.put("pendingApplications", 0);
            stats.put("approvedApplications", 0);
            stats.put("rejectedApplications", 0);
        }
        return stats;
    }

    public List<Map<String, String>> getRecentApplicationsForCompany(User companyUser, int limit) {
        List<Map<String, String>> applications = new ArrayList<>();
        try {
            List<Application> allApplications = getAllApplicationsForCompany(companyUser);
            allApplications.sort((a, b) -> b.getCreated_at().compareTo(a.getCreated_at()));

            for (int i = 0; i < Math.min(limit, allApplications.size()); i++) {
                Application app = allApplications.get(i);
                Map<String, String> appData = new HashMap<>();
                appData.put("applicantName", app.getApplicant().getName());
                appData.put("jobTitle", app.getJob().getJobTitle());
                appData.put("status", app.getApplicationStatus().toString());
                appData.put("appliedDate", formatRelativeDate(app.getCreated_at()));
                appData.put("statusClass", getStatusClass(app.getApplicationStatus()));
                applications.add(appData);
            }
        } catch (Exception e) {
            // Return empty list if query fails
        }
        return applications;
    }

    public List<Map<String, String>> getRecentActivitiesForCompany(User companyUser, int limit) {
        List<Map<String, String>> activities = new ArrayList<>();
        try {
            List<Application> allApplications = getAllApplicationsForCompany(companyUser);
            allApplications.sort((a, b) -> b.getCreated_at().compareTo(a.getCreated_at()));

            for (int i = 0; i < Math.min(limit, allApplications.size()); i++) {
                Application app = allApplications.get(i);
                Map<String, String> activity = new HashMap<>();
                activity.put("type", "application");
                activity.put("icon", "fa-file-alt");
                activity.put("title", "New application for " + app.getJob().getJobTitle());
                activity.put("date", formatRelativeDate(app.getCreated_at()));
                activity.put("description",
                    "Applicant: " + app.getApplicant().getName() +
                    " | Status: " + app.getApplicationStatus());
                activities.add(activity);
            }

            if (activities.isEmpty()) {
                Map<String, String> placeholder = new HashMap<>();
                placeholder.put("type", "info");
                placeholder.put("icon", "fa-info-circle");
                placeholder.put("title", "No Recent Activity");
                placeholder.put("date", "Today");
                placeholder.put("description", "Post a job to start receiving applications");
                activities.add(placeholder);
            }
        } catch (Exception e) {
            // Return empty list if query fails
        }
        return activities;
    }

    public Application updateApplicationStatus(String applicationId, String newStatus, User companyUser) {
        Application application = applicationDAO.findById(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }

        List<Company> companies = companyDAO.findByUser(companyUser);
        if (companies.isEmpty()) {
            throw new IllegalStateException("No company associated with user");
        }

        Company company = companies.get(0);
        Job job = application.getJob();
        if (job == null || !job.getCompany().getId().equals(company.getId())) {
            throw new IllegalStateException("Application does not belong to your company");
        }

        Status status = Status.valueOf(newStatus.toUpperCase());
        application.setApplicationStatus(status);
        Application updated = applicationDAO.update(application);

        // Send shortlisted email if application is approved
        if (status == Status.APPROVED) {
            User applicant = application.getApplicant();
            if (applicant != null && job != null) {
                emailService.sendApplicationShortlistedEmail(
                    applicant.getEmail(),
                    applicant.getName(),
                    job.getJobTitle(),
                    company.getName()
                );
            }
        }

        MainLogger.logServiceOperation("ApplicationService", "UPDATE_STATUS", true,
            "Application: " + applicationId + " to " + status);
        return updated;
    }

    public Application getApplicationForCompany(String applicationId, User companyUser) {
        Application application = applicationDAO.findById(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }

        List<Company> companies = companyDAO.findByUser(companyUser);
        if (companies.isEmpty()) {
            throw new IllegalStateException("No company associated with user");
        }

        Company company = companies.get(0);
        Job job = application.getJob();
        if (job == null || job.getCompany() == null || !job.getCompany().getId().equals(company.getId())) {
            throw new IllegalStateException("Application does not belong to your company");
        }

        return application;
    }

    public boolean validateApplicantForCompany(User applicant, User companyUser) {
        List<Company> companies = companyDAO.findByUser(companyUser);
        if (companies.isEmpty()) {
            return false;
        }

        Company company = companies.get(0);
        List<Application> applicantApplications = applicationDAO.findByApplicant(applicant);
        return applicantApplications.stream()
            .anyMatch(app -> app.getJob() != null &&
                app.getJob().getCompany() != null &&
                app.getJob().getCompany().getId().equals(company.getId()));
    }

    public User getApplicantById(String applicantId) {
        return userDAO.findById(applicantId);
    }

    private String formatRelativeDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    private String getStatusClass(Status status) {
        switch (status) {
            case PENDING:
                return "pending";
            case APPROVED:
                return "accepted";
            case REJECTED:
                return "rejected";
            default:
                return "pending";
        }
    }
}
