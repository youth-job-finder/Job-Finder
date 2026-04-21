package com.jakartaee.jobfinder.servlet.applicant;

import com.jakartaee.jobfinder.dao.ApplicationDAO;
import com.jakartaee.jobfinder.dao.JobDAO;
import com.jakartaee.jobfinder.entity.Application;
import com.jakartaee.jobfinder.entity.Job;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.entity.status.Status;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.AuthService;
import com.jakartaee.jobfinder.services.EmailService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for handling job applications by applicants.
 * Allows applicants to view the application form and submit job applications.
 */
@WebServlet("/applicant/apply")
public class ApplyJobServlet extends HttpServlet {

    private static final String SERVLET_NAME = "ApplyJobServlet";

    @Inject
    private AuthService authService;

    @Inject
    private JobDAO jobDAO;

    @Inject
    private ApplicationDAO applicationDAO;

    @Inject
    private EmailService emailService;

    /**
     * Displays the job application form for a specific job.
     * Verifies the job exists and checks if the user has already applied.
     *
     * @param req  the HTTP request containing the jobId parameter
     * @param resp the HTTP response for rendering the application form
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = authService.requireApplicant(req, resp, SERVLET_NAME);
        if (user == null) {
            return;
        }

        String currentUserId = user.getId();
        String jobId = req.getParameter("jobId");
        if (jobId == null || jobId.trim().isEmpty()) {
            MainLogger.logError(SERVLET_NAME, "Apply job page requested without jobId by user: " + currentUserId);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required");
            return;
        }

        try {
            Job job = jobDAO.findById(jobId);
            if (job == null) {
                MainLogger.logError(SERVLET_NAME, "Job not found for jobId: " + jobId + ", requested by user: " + currentUserId);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Job not found");
                return;
            }

            // Check if user has already applied for this job
            List<Application> existingApplications = applicationDAO.findByApplicant(user);
            boolean alreadyApplied = existingApplications.stream()
                .anyMatch(app -> app.getJob() != null && app.getJob().getId().equals(jobId));

            if (alreadyApplied) {
                MainLogger.logInfo(SERVLET_NAME, "User " + currentUserId + " already applied for job " + jobId);
                req.setAttribute("error", "You have already applied for this job.");
            }

            req.setAttribute("job", job);
            req.setAttribute("isAuthenticated", true);
            req.setAttribute("role", Role.APPLICANT.name());
            req.getRequestDispatcher("/views/applicant/apply-job.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logError(SERVLET_NAME, "Error loading apply job page for jobId: " + jobId, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load job details");
        }
    }

    /**
     * Processes a job application submission.
     * Validates the request, checks for duplicate applications, and creates a new application.
     *
     * @param req  the HTTP request containing jobId and application data
     * @param resp the HTTP response for redirecting after successful submission
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = authService.requireApplicant(req, resp, SERVLET_NAME);
        if (user == null) {
            return;
        }

        String currentUserId = user.getId();
        String jobId = req.getParameter("jobId");
        if (jobId == null || jobId.trim().isEmpty()) {
            MainLogger.logError(SERVLET_NAME, "Job application submitted without jobId by user: " + currentUserId);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required");
            return;
        }

        MainLogger.logInfo(SERVLET_NAME, "Job application initiated by user: " + currentUserId + " for job: " + jobId);

        try {
            Job job = jobDAO.findById(jobId);
            if (job == null) {
                MainLogger.logError(SERVLET_NAME, "Job not found for jobId: " + jobId + ", application by user: " + currentUserId);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Job not found");
                return;
            }

            // Check if user has already applied for this job
            List<Application> existingApplications = applicationDAO.findByApplicant(user);
            boolean alreadyApplied = existingApplications.stream()
                .anyMatch(app -> app.getJob() != null && app.getJob().getId().equals(jobId));

            if (alreadyApplied) {
                MainLogger.logInfo(SERVLET_NAME, "Duplicate application blocked - user: " + currentUserId + " already applied for job: " + jobId);
                req.setAttribute("error", "You have already applied for this job.");
                req.setAttribute("job", job);
                req.setAttribute("isAuthenticated", true);
                req.setAttribute("role", Role.APPLICANT.name());
                req.getRequestDispatcher("/views/applicant/apply-job.jsp").forward(req, resp);
                return;
            }

            // Create new application
            Application application = new Application(user, job, Status.PENDING);
            applicationDAO.create(application);

            // Send email notifications
            String companyName = job.getCompany() != null ? job.getCompany().getName() : "Unknown Company";
            String companyAdminEmail = job.getCompany() != null && job.getCompany().getCompanyAdmin() != null 
                ? job.getCompany().getCompanyAdmin().getEmail() 
                : null;

            // Notify applicant
            emailService.sendApplicationSubmittedEmail(
                user.getEmail(), 
                user.getName(), 
                job.getJobTitle(), 
                companyName
            );
            MainLogger.logInfo(SERVLET_NAME, "Application submission email sent to applicant: " + user.getEmail());

            // Notify company admin (if email available)
            if (companyAdminEmail != null) {
                emailService.sendNewApplicationNotificationEmail(
                    companyAdminEmail,
                    user.getName(),
                    job.getJobTitle(),
                    companyName
                );
                MainLogger.logInfo(SERVLET_NAME, "New application notification email sent to company admin: " + companyAdminEmail);
            }

            MainLogger.logUserAction(SERVLET_NAME, currentUserId, "APPLY_JOB: " + jobId);
            MainLogger.logInfo(SERVLET_NAME, "Job application successful - user: " + currentUserId + ", job: " + jobId + ", company: " + companyName);

            // Redirect to applications page with success message
            resp.sendRedirect(req.getContextPath() + "/applicant/applications?success=Application+submitted+successfully");

        } catch (Exception e) {
            MainLogger.logError(SERVLET_NAME, "Failed to submit job application - user: " + currentUserId + ", job: " + jobId, e);
            req.setAttribute("error", "Failed to submit application. Please try again.");
            req.getRequestDispatcher("/views/applicant/apply-job.jsp").forward(req, resp);
        }
    }
}
