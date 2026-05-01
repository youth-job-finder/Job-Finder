package com.jakartaee.jobfinder.services;

import com.jakartaee.jobfinder.dao.ApplicationDAO;
import com.jakartaee.jobfinder.dao.CompanyDAO;
import com.jakartaee.jobfinder.dao.JobDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.models.Application;
import com.jakartaee.jobfinder.models.Company;
import com.jakartaee.jobfinder.models.Job;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.models.status.Status;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminService {

    @Inject
    private UserDAO userDAO;

    @Inject
    private CompanyDAO companyDAO;

    @Inject
    private JobDAO jobDAO;

    @Inject
    private ApplicationDAO applicationDAO;

    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.putAll(buildUserStats());
            stats.putAll(buildCompanyStats());
            stats.putAll(buildJobStats());
            stats.putAll(buildApplicationStats());
        } catch (Exception e) {
            MainLogger.logServiceOperation("AdminService", "GET_DASHBOARD_STATS", false, e.getMessage());
        }
        return stats;
    }

    public Map<String, Object> getFullReports() {
        Map<String, Object> reports = new HashMap<>();
        try {
            reports.put("userStats", buildUserStats());
            reports.put("companyStats", buildCompanyStats());
            reports.put("jobStats", buildJobStats());
            reports.put("applicationStats", buildApplicationStats());
            reports.put("activityTrends", buildActivityTrends());
        } catch (Exception e) {
            MainLogger.logServiceOperation("AdminService", "GET_FULL_REPORTS", false, e.getMessage());
        }
        return reports;
    }

    private Map<String, Object> buildUserStats() {
        Map<String, Object> stats = new HashMap<>();
        List<User> users = userDAO.findAll();

        stats.put("totalUsers", users.size());
        stats.put("applicants", users.stream().filter(u -> u.getRole() == Role.APPLICANT).count());
        stats.put("companyAdmins", users.stream().filter(u -> u.getRole() == Role.COMPANY_ADMIN).count());
        stats.put("systemAdmins", users.stream().filter(u -> u.getRole() == Role.SYSTEM_ADMIN).count());
        stats.put("verifiedUsers", users.stream().filter(User::getEmailVerified).count());
        stats.put("unverifiedUsers", users.stream().filter(u -> !u.getEmailVerified()).count());

        LocalDateTime weekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        LocalDateTime monthAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);

        stats.put("newUsersThisWeek", users.stream().filter(u -> u.getCreatedAt().isAfter(weekAgo)).count());
        stats.put("newUsersThisMonth", users.stream().filter(u -> u.getCreatedAt().isAfter(monthAgo)).count());

        return stats;
    }

    private Map<String, Object> buildCompanyStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Company> companies = companyDAO.findAll();

        stats.put("totalCompanies", companies.size());
        stats.put("pendingCompanies", companies.stream().filter(c -> c.getStatus() == Status.PENDING).count());
        stats.put("approvedCompanies", companies.stream().filter(c -> c.getStatus() == Status.APPROVED).count());
        stats.put("rejectedCompanies", companies.stream().filter(c -> c.getStatus() == Status.REJECTED).count());
        stats.put("urlVerifiedCompanies", companies.stream().filter(Company::getUrlVerified).count());

        return stats;
    }

    private Map<String, Object> buildJobStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Job> jobs = jobDAO.findAll();

        stats.put("totalJobs", jobs.size());
        stats.put("activeJobs", jobs.size());

        Map<String, Long> jobsByLocation = jobs.stream()
            .collect(Collectors.groupingBy(Job::getPhysicalAddress, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        stats.put("jobsByLocation", jobsByLocation);

        return stats;
    }

    private Map<String, Object> buildApplicationStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Application> applications = applicationDAO.findAll();

        stats.put("totalApplications", applications.size());
        stats.put("pendingApplications", applications.stream().filter(a -> a.getApplicationStatus() == Status.PENDING).count());
        stats.put("approvedApplications", applications.stream().filter(a -> a.getApplicationStatus() == Status.APPROVED).count());
        stats.put("rejectedApplications", applications.stream().filter(a -> a.getApplicationStatus() == Status.REJECTED).count());

        return stats;
    }

    public List<Map<String, String>> getRecentSystemActivities(int limit) {
        List<Map<String, String>> activities = new ArrayList<>();
        try {
            List<User> users = userDAO.findAll();
            List<Company> companies = companyDAO.findAll();
            List<Job> jobs = jobDAO.findAll();
            List<Application> applications = applicationDAO.findAll();

            // Add recent user registrations
            users.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .forEach(u -> {
                    Map<String, String> activity = new HashMap<>();
                    activity.put("type", "user");
                    activity.put("icon", "fa-user-plus");
                    activity.put("iconColor", "green");
                    activity.put("title", "New User Registered");
                    activity.put("description", u.getName() + " joined as " + u.getRole());
                    activity.put("date", formatRelativeDate(u.getCreatedAt()));
                    activities.add(activity);
                });

            // Add recent company registrations
            companies.stream()
                .filter(c -> c.getCreated_at() != null)
                .sorted((a, b) -> b.getCreated_at().compareTo(a.getCreated_at()))
                .limit(limit)
                .forEach(c -> {
                    Map<String, String> activity = new HashMap<>();
                    activity.put("type", "company");
                    activity.put("icon", "fa-building");
                    activity.put("iconColor", c.getStatus() == Status.APPROVED ? "blue" : "orange");
                    activity.put("title", "Company " + (c.getStatus() == Status.APPROVED ? "Approved" : "Pending"));
                    activity.put("description", c.getName() + " - " + c.getIndustry());
                    activity.put("date", formatRelativeDate(c.getCreated_at()));
                    activities.add(activity);
                });

            // Add recent job postings
            jobs.stream()
                .sorted((a, b) -> b.getCreated_at().compareTo(a.getCreated_at()))
                .limit(limit)
                .forEach(j -> {
                    Map<String, String> activity = new HashMap<>();
                    activity.put("type", "job");
                    activity.put("icon", "fa-briefcase");
                    activity.put("iconColor", "purple");
                    activity.put("title", "New Job Posted");
                    activity.put("description", j.getJobTitle() + " at " + j.getCompany().getName());
                    activity.put("date", formatRelativeDate(j.getCreated_at()));
                    activities.add(activity);
                });

            // Add recent applications
            applications.stream()
                .sorted((a, b) -> b.getCreated_at().compareTo(a.getCreated_at()))
                .limit(limit)
                .forEach(a -> {
                    Map<String, String> activity = new HashMap<>();
                    activity.put("type", "application");
                    activity.put("icon", "fa-paper-plane");
                    activity.put("iconColor", "teal");
                    activity.put("title", "New Application");
                    activity.put("description", a.getApplicant().getName() + " applied for " + a.getJob().getJobTitle());
                    activity.put("date", formatRelativeDate(a.getCreated_at()));
                    activities.add(activity);
                });

            // Sort all activities by date and limit
            activities.sort((a, b) -> {
                LocalDateTime dateA = parseRelativeDate(a.get("date"));
                LocalDateTime dateB = parseRelativeDate(b.get("date"));
                return dateB.compareTo(dateA);
            });

            if (activities.size() > limit) {
                return activities.subList(0, limit);
            }

        } catch (Exception e) {
            MainLogger.logServiceOperation("AdminService", "GET_RECENT_ACTIVITIES", false, e.getMessage());
        }
        return activities;
    }

    private String formatRelativeDate(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown";
        LocalDateTime now = LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(dateTime, now);

        if (duration.toDays() > 0) {
            return duration.toDays() + " days ago";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + " hours ago";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + " minutes ago";
        } else {
            return "Just now";
        }
    }

    private LocalDateTime parseRelativeDate(String relativeDate) {
        if (relativeDate == null || relativeDate.equals("Unknown")) {
            return LocalDateTime.MIN;
        }
        if (relativeDate.equals("Just now")) {
            return LocalDateTime.now();
        }
        try {
            int value = Integer.parseInt(relativeDate.split(" ")[0]);
            if (relativeDate.contains("days")) {
                return LocalDateTime.now().minusDays(value);
            } else if (relativeDate.contains("hours")) {
                return LocalDateTime.now().minusHours(value);
            } else if (relativeDate.contains("minutes")) {
                return LocalDateTime.now().minusMinutes(value);
            }
        } catch (Exception e) {
            // Return minimum date if parsing fails
        }
        return LocalDateTime.MIN;
    }

    private Map<String, Object> buildActivityTrends() {
        Map<String, Object> trends = new HashMap<>();
        LocalDateTime monthAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);

        List<User> recentUsers = userDAO.findAll().stream()
            .filter(u -> u.getCreatedAt().isAfter(monthAgo))
            .toList();

        List<Job> recentJobs = jobDAO.findAll().stream()
            .filter(j -> j.getCreated_at().isAfter(monthAgo))
            .toList();

        List<Application> recentApplications = applicationDAO.findAll().stream()
            .filter(a -> a.getCreated_at().isAfter(monthAgo))
            .toList();

        trends.put("recentUsers", recentUsers.size());
        trends.put("recentJobs", recentJobs.size());
        trends.put("recentApplications", recentApplications.size());

        return trends;
    }
}
