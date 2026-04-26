package com.jakartaee.jobfinder.services;

import com.jakartaee.jobfinder.dao.ApplicationDAO;
import com.jakartaee.jobfinder.dao.CompanyDAO;
import com.jakartaee.jobfinder.dao.JobDAO;
import com.jakartaee.jobfinder.dao.SavedJobDAO;
import com.jakartaee.jobfinder.entity.Application;
import com.jakartaee.jobfinder.entity.Company;
import com.jakartaee.jobfinder.entity.Job;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.util.SalaryFormatter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service for managing job-related operations in the JobFinder application.
 * Provides methods for job CRUD operations, job searches, and job-related queries.
 */
@ApplicationScoped
public class JobService {

    @Inject
    private JobDAO jobDAO;

    @Inject
    private CompanyDAO companyDAO;

    @Inject
    private ApplicationDAO applicationDAO;

    @Inject
    private SavedJobDAO savedJobDAO;

    /**
     * Retrieves all jobs from the database.
     *
     * @return list of all jobs
     */
    public List<Job> getAllJobs() {
        return jobDAO.findAll();
    }

    /**
     * Retrieves only jobs marked as remote.
     *
     * @return list of remote jobs
     */
    public List<Job> getRemoteJobs() {
        return jobDAO.findByJobType("Remote");
    }

    /**
     * Keyword search across job fields and company name.
     *
     * @param query search text; blank returns all jobs (same as getAllJobs)
     */
    public List<Job> searchJobs(String query) {
        if (query == null || query.isBlank()) {
            return jobDAO.findAll();
        }
        return jobDAO.searchByTerm(query);
    }

    /**
     * Keyword search limited to remote jobs.
     *
     * @param query search text; blank returns all remote jobs
     */
    public List<Job> searchRemoteJobs(String query) {
        if (query == null || query.isBlank()) {
            return jobDAO.findByJobType("Remote");
        }
        return jobDAO.searchByTermAndJobType(query, "Remote");
    }

    /**
     * Retrieves a job by its ID.
     *
     * @param jobId the job ID
     * @return the job entity, or null if not found
     */
    public Job getJobById(String jobId) {
        return jobDAO.findById(jobId);
    }

    /**
     * Checks if a user has already applied for a specific job.
     *
     * @param jobId  the job ID
     * @param userId the user ID
     * @return true if user has applied, false otherwise
     */
    public boolean hasUserApplied(String jobId, String userId) {
        return applicationDAO.existsByApplicantAndJob(userId, jobId);
    }

    /**
     * Retrieves all jobs for a specific company.
     *
     * @param company the company entity
     * @return list of jobs belonging to the company
     */
    public List<Job> getJobsByCompany(Company company) {
        return jobDAO.findByCompany(company);
    }

    /**
     * Retrieves all jobs for a company associated with the given user.
     *
     * @param user the company admin user
     * @return list of jobs belonging to the user's company
     */
    public List<Job> getJobsByCompanyUser(User user) {
        List<Company> companies = companyDAO.findByUser(user);
        if (companies.isEmpty()) {
            return List.of();
        }
        return jobDAO.findByCompany(companies.get(0));
    }

    /**
     * Creates a new job posting for a company.
     *
     * @param user            the company admin creating the job
     * @param jobTitle        the job title
     * @param jobDescription  the job description
     * @param jobRequirements the job requirements
     * @param physicalAddress the job location
     * @param salaryRange     the salary range
     * @param jobType         the type of job
     * @return the created Job entity
     * @throws IllegalStateException if no company is associated with the user
     */
    public Job createJob(User user, String jobTitle, String jobDescription, String jobRequirements, String physicalAddress, String salaryRange, String jobType) {
        List<Company> companies = companyDAO.findByUser(user);
        if (companies.isEmpty()) {
            throw new IllegalStateException("No company associated with this user");
        }

        Company company = companies.get(0);
        Job job = new Job();
        job.setJobTitle(jobTitle.trim());
        job.setJobDescription(jobDescription.trim());
        job.setJobRequirements(jobRequirements != null ? jobRequirements.trim() : "");
        job.setPhysicalAddress(physicalAddress != null ? physicalAddress.trim() : "");
        job.setSalaryRange(SalaryFormatter.normalizeToRand(
                salaryRange != null && !salaryRange.trim().isEmpty() ? salaryRange.trim() : "Competitive"));
        job.setJobType(jobType != null && !jobType.trim().isEmpty() ? jobType.trim() : "Full-time");
        job.setCompany(company);

        jobDAO.create(job);
        MainLogger.logServiceOperation("JobService", "CREATE_JOB", true, "Job: " + jobTitle + ", Company: " + company.getName());
        return job;
    }

    public Job updateJob(String jobId, String jobTitle, String jobDescription, String jobRequirements, String physicalAddress, String salaryRange, String jobType) {
        Job job = jobDAO.findById(jobId);
        if (job == null) {
            throw new IllegalStateException("Job not found");
        }

        job.setJobTitle(jobTitle.trim());
        job.setJobDescription(jobDescription.trim());
        job.setJobRequirements(jobRequirements != null ? jobRequirements.trim() : "");
        job.setPhysicalAddress(physicalAddress != null ? physicalAddress.trim() : "");
        job.setSalaryRange(SalaryFormatter.normalizeToRand(
                salaryRange != null && !salaryRange.trim().isEmpty() ? salaryRange.trim() : "Competitive"));
        job.setJobType(jobType != null && !jobType.trim().isEmpty() ? jobType.trim() : "Full-time");

        jobDAO.update(job);
        MainLogger.logServiceOperation("JobService", "UPDATE_JOB", true, "Job ID: " + jobId + ", Title: " + jobTitle);
        return job;
    }

    public boolean isJobOwnedByUser(String jobId, User user) {
        Job job = jobDAO.findById(jobId);
        if (job == null) {
            return false;
        }

        List<Company> userCompanies = companyDAO.findByUser(user);
        return userCompanies.stream().anyMatch(company -> company.getId().equals(job.getCompany().getId()));
    }

    public boolean deleteJob(String jobId, User user) {
        Job job = jobDAO.findById(jobId);
        if (job == null) {
            throw new IllegalStateException("Job not found");
        }

        if (!isJobOwnedByUser(jobId, user)) {
            throw new IllegalStateException("You can only delete your own jobs");
        }

        deleteJobInternal(job);
        MainLogger.logServiceOperation("JobService", "DELETE_JOB", true, "Job ID: " + jobId);
        return true;
    }

    public boolean deleteJobAsAdmin(String jobId) {
        Job job = jobDAO.findById(jobId);
        if (job == null) {
            throw new IllegalStateException("Job not found");
        }

        deleteJobInternal(job);
        MainLogger.logServiceOperation("JobService", "DELETE_JOB_ADMIN", true, "Job ID: " + jobId);
        return true;
    }

    private void deleteJobInternal(Job job) {
        List<Application> applications = applicationDAO.findByJob(job);
        if (!applications.isEmpty()) {
            throw new IllegalStateException("This job cannot be deleted because applicants have already applied to it.");
        }

        int removedSavedJobs = savedJobDAO.deleteByJobId(job.getId());
        MainLogger.logServiceOperation("JobService", "DELETE_JOB_SAVED_CLEANUP", true,
                "Job ID: " + job.getId() + ", Removed saved jobs: " + removedSavedJobs);

        jobDAO.delete(job.getId());
    }

    public Map<String, Integer> getJobStatistics(User user) {
        Map<String, Integer> stats = new HashMap<>();
        try {
            List<Company> userCompanies = companyDAO.findByUser(user);
            if (!userCompanies.isEmpty()) {
                Company company = userCompanies.get(0);
                List<Job> companyJobs = jobDAO.findByCompany(company);
                stats.put("totalJobs", companyJobs.size());

                LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
                List<Job> recentJobs = companyJobs.stream()
                    .filter(job -> job.getCreated_at().isAfter(thirtyDaysAgo))
                    .toList();
                stats.put("recentJobs", recentJobs.size());
            } else {
                stats.put("totalJobs", 0);
                stats.put("recentJobs", 0);
            }
        } catch (Exception e) {
            stats.put("totalJobs", 0);
            stats.put("recentJobs", 0);
        }
        return stats;
    }

    public List<Map<String, String>> getRecentJobsForCompany(User user, int limit) {
        List<Map<String, String>> jobs = new ArrayList<>();
        try {
            List<Company> userCompanies = companyDAO.findByUser(user);
            MainLogger.logServiceOperation("JobService", "GET_RECENT_JOBS", true, "Found " + userCompanies.size() + " companies for user " + user.getId());
            
            if (!userCompanies.isEmpty()) {
                Company company = userCompanies.get(0);
                List<Job> companyJobs = jobDAO.findByCompany(company);
                MainLogger.logServiceOperation("JobService", "GET_RECENT_JOBS", true, "Found " + companyJobs.size() + " jobs for company " + company.getId());
                
                companyJobs.sort((a, b) -> b.getCreated_at().compareTo(a.getCreated_at()));

                for (int i = 0; i < Math.min(limit, companyJobs.size()); i++) {
                    Job job = companyJobs.get(i);
                    Map<String, String> jobData = new HashMap<>();
                    jobData.put("id", job.getId().toString());
                    jobData.put("title", job.getJobTitle());
                    jobData.put("description", truncateString(job.getJobDescription(), 100));
                    jobData.put("location", job.getPhysicalAddress());
                    jobData.put("postedDate", formatRelativeDate(job.getCreated_at()));

                    List<Application> jobApplications = applicationDAO.findByJob(job);
                    jobData.put("applicationCount", String.valueOf(jobApplications.size()));

                    jobs.add(jobData);
                }
            } else {
                MainLogger.logServiceOperation("JobService", "GET_RECENT_JOBS", false, "No company found for user " + user.getId());
            }
        } catch (Exception e) {
            MainLogger.logServiceOperation("JobService", "GET_RECENT_JOBS", false, "Error: " + e.getMessage());
        }
        return jobs;
    }

    private String formatRelativeDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    private String truncateString(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
}
