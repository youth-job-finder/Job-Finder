package com.jakartaee.jobfinder.services;

import com.jakartaee.jobfinder.dao.ApplicationDAO;
import com.jakartaee.jobfinder.dao.JobDAO;
import com.jakartaee.jobfinder.dao.SavedJobDAO;
import com.jakartaee.jobfinder.models.Application;
import com.jakartaee.jobfinder.models.Job;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.status.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ApplicantService {

    @Inject
    private ApplicationDAO applicationDAO;

    @Inject
    private JobDAO jobDAO;

    @Inject
    private SavedJobDAO savedJobDAO;

    public Map<String, Integer> getDashboardStats(User user) {
        Map<String, Integer> stats = new HashMap<>();
        try {
            List<Application> applications = applicationDAO.findByApplicant(user);
            stats.put("applicationsSent", applications.size());

            long pendingCount = applications.stream()
                .filter(app -> app.getApplicationStatus() == Status.PENDING)
                .count();
            long approvedCount = applications.stream()
                .filter(app -> app.getApplicationStatus() == Status.APPROVED)
                .count();

            // Get actual saved jobs count from database
            long savedJobsCount = savedJobDAO.countByApplicantId(user.getId());

            stats.put("profileViews", 0);
            stats.put("savedJobs", (int) savedJobsCount);
            stats.put("interviewsScheduled", (int) approvedCount);

        } catch (Exception e) {
            stats.put("applicationsSent", 0);
            stats.put("profileViews", 0);
            stats.put("savedJobs", 0);
            stats.put("interviewsScheduled", 0);
        }
        return stats;
    }

    public List<Map<String, String>> getRecentActivities(User user) {
        List<Map<String, String>> activities = new ArrayList<>();
        try {
            List<Application> applications = applicationDAO.findByApplicant(user);
            applications.sort((a, b) -> b.getCreated_at().compareTo(a.getCreated_at()));

            for (int i = 0; i < Math.min(5, applications.size()); i++) {
                Application app = applications.get(i);
                Map<String, String> activity = new HashMap<>();

                activity.put("type", "applied");
                activity.put("icon", "fa-paper-plane");
                activity.put("title", "Applied for " + app.getJob().getJobTitle());
                activity.put("date", formatRelativeDate(app.getCreated_at()));
                activity.put("description", app.getJob().getCompany().getName() + " - " + app.getJob().getPhysicalAddress());

                activities.add(activity);
            }
        } catch (Exception e) {
            // Return empty list if query fails
        }
        return activities;
    }

    public List<Map<String, String>> getRecommendedJobs(int limit) {
        List<Map<String, String>> jobs = new ArrayList<>();
        try {
            List<Job> latestJobs = jobDAO.findAll();
            latestJobs.sort((a, b) -> b.getCreated_at().compareTo(a.getCreated_at()));

            for (int i = 0; i < Math.min(limit, latestJobs.size()); i++) {
                Job job = latestJobs.get(i);
                Map<String, String> jobData = new HashMap<>();

                jobData.put("id", job.getId());
                jobData.put("title", job.getJobTitle());
                jobData.put("company", job.getCompany().getName());
                jobData.put("type", "Full-time");
                jobData.put("typeClass", "full-time");
                jobData.put("location", job.getPhysicalAddress());
                jobData.put("salary", "Competitive");
                jobData.put("description", truncateString(job.getJobDescription(), 100));

                jobs.add(jobData);
            }
        } catch (Exception e) {
            // Return empty list if query fails
        }
        return jobs;
    }

    private String formatRelativeDate(LocalDateTime dateTime) {
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

    private String truncateString(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
}
