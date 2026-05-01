package com.jakartaee.jobfinder.services;

import com.jakartaee.jobfinder.dao.ApplicationDAO;
import com.jakartaee.jobfinder.dao.CompanyDAO;
import com.jakartaee.jobfinder.dao.JobDAO;
import com.jakartaee.jobfinder.dao.ReviewDAO;
import com.jakartaee.jobfinder.models.Application;
import com.jakartaee.jobfinder.models.Company;
import com.jakartaee.jobfinder.models.Job;
import com.jakartaee.jobfinder.models.Review;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.status.Status;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CompanyService {

    @Inject
    private CompanyDAO companyDAO;

    @Inject
    private JobDAO jobDAO;

    @Inject
    private ApplicationDAO applicationDAO;

    @Inject
    private ReviewDAO reviewDAO;

    public Company getCompanyByUser(User user) {
        List<Company> companies = companyDAO.findByUser(user);
        return companies.isEmpty() ? null : companies.get(0);
    }

    public Map<String, Integer> getCompanyDashboardStats(User user) {
        Map<String, Integer> stats = new HashMap<>();
        try {
            Company company = getCompanyByUser(user);
            if (company != null) {
                List<Job> companyJobs = jobDAO.findByCompany(company);
                stats.put("totalJobs", companyJobs.size());

                List<Application> allApplications = new ArrayList<>();
                for (Job job : companyJobs) {
                    allApplications.addAll(applicationDAO.findByJob(job));
                }
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

                java.time.LocalDateTime thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30);
                List<Job> recentJobs = companyJobs.stream()
                    .filter(job -> job.getCreated_at().isAfter(thirtyDaysAgo))
                    .toList();
                stats.put("recentJobs", recentJobs.size());
            } else {
                setEmptyStats(stats);
            }
        } catch (Exception e) {
            setEmptyStats(stats);
        }
        return stats;
    }

    public Map<String, Object> getCompanyAnalytics(User user) {
        Map<String, Object> analytics = new HashMap<>();
        try {
            Company company = getCompanyByUser(user);
            if (company != null) {
                List<Job> jobs = jobDAO.findByCompany(company);
                analytics.put("totalJobs", jobs.size());

                List<Application> allApplications = new ArrayList<>();
                for (Job job : jobs) {
                    allApplications.addAll(applicationDAO.findByJob(job));
                }
                analytics.put("totalApplications", allApplications.size());

                long pendingCount = allApplications.stream()
                    .filter(app -> app.getApplicationStatus() == Status.PENDING)
                    .count();
                long approvedCount = allApplications.stream()
                    .filter(app -> app.getApplicationStatus() == Status.APPROVED)
                    .count();
                long rejectedCount = allApplications.stream()
                    .filter(app -> app.getApplicationStatus() == Status.REJECTED)
                    .count();

                analytics.put("pendingApplications", pendingCount);
                analytics.put("approvedApplications", approvedCount);
                analytics.put("rejectedApplications", rejectedCount);

                List<Review> reviews = reviewDAO.findByCompany(company);
                analytics.put("totalReviews", reviews.size());

                if (!reviews.isEmpty()) {
                    double avgRating = reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);
                    analytics.put("averageRating", avgRating);
                } else {
                    analytics.put("averageRating", 0.0);
                }
            }
        } catch (Exception e) {
            MainLogger.logServiceOperation("CompanyService", "GET_ANALYTICS", false, e.getMessage());
        }
        return analytics;
    }

    private void setEmptyStats(Map<String, Integer> stats) {
        stats.put("totalJobs", 0);
        stats.put("totalApplications", 0);
        stats.put("pendingApplications", 0);
        stats.put("approvedApplications", 0);
        stats.put("rejectedApplications", 0);
        stats.put("recentJobs", 0);
    }
}
