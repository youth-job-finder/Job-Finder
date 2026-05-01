package com.jakartaee.jobfinder.servlet.company;

import com.jakartaee.jobfinder.models.Job;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.AuthService;
import com.jakartaee.jobfinder.services.JobService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet for editing existing job postings by company administrators.
 * Handles displaying the job edit form and processing job updates.
 */
@WebServlet("/company/edit-job")
public class CompanyEditJobServlet extends HttpServlet {

    private static final String SERVLET_NAME = "CompanyEditJobServlet";

    @Inject
    private AuthService authService;

    @Inject
    private JobService jobService;

    /**
     * Displays the job edit form for a specific job.
     * Verifies the job belongs to the user's company.
     *
     * @param req  the HTTP request containing the job id parameter
     * @param resp the HTTP response for rendering the edit form
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = authService.requireCompanyAdmin(req, resp, SERVLET_NAME);
        if (user == null) {
            return;
        }

        String currentUserId = user.getId();
        String jobId = req.getParameter("id");
        if (jobId == null || jobId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/company/listings?error=Job ID is required");
            return;
        }

        Job job = jobService.getJobById(jobId);
        if (job == null) {
            resp.sendRedirect(req.getContextPath() + "/company/listings?error=Job not found");
            return;
        }

        // Verify the job belongs to the user's company
        if (!jobService.isJobOwnedByUser(jobId, user)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only edit your own jobs");
            return;
        }

        req.setAttribute("job", job);
        req.setAttribute("isAuthenticated", true);
        req.setAttribute("role", Role.COMPANY_ADMIN.name());

        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_EDIT_JOB_PAGE: " + jobId);

        req.getRequestDispatcher("/views/company/edit-job.jsp").forward(req, resp);
    }

    /**
     * Processes job update submissions.
     * Validates required fields, verifies job ownership, and updates the job.
     *
     * @param req  the HTTP request containing job update data
     * @param resp the HTTP response for redirecting after update
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = authService.requireCompanyAdmin(req, resp, SERVLET_NAME);
        if (user == null) {
            return;
        }

        String currentUserId = user.getId();

        try {
            String jobId = req.getParameter("jobId");
            String jobTitle = req.getParameter("jobTitle");
            String jobDescription = req.getParameter("jobDescription");
            String jobRequirements = req.getParameter("jobRequirements");
            String physicalAddress = req.getParameter("physicalAddress");
            String salaryRange = req.getParameter("salaryRange");
            String jobType = req.getParameter("jobType");

            if (jobId == null || jobId.trim().isEmpty() ||
                jobTitle == null || jobTitle.trim().isEmpty() ||
                jobDescription == null || jobDescription.trim().isEmpty()) {
                req.setAttribute("error", "Job ID, title and description are required");
                req.getRequestDispatcher("/views/company/edit-job.jsp").forward(req, resp);
                return;
            }

            // Verify the job belongs to the user's company
            if (!jobService.isJobOwnedByUser(jobId, user)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only edit your own jobs");
                return;
            }

            Job updatedJob = jobService.updateJob(jobId, jobTitle, jobDescription, jobRequirements, 
                                                  physicalAddress, salaryRange, jobType);

            MainLogger.logUserAction(SERVLET_NAME, currentUserId, "UPDATE_JOB: " + jobId + " - " + jobTitle);
            resp.sendRedirect(req.getContextPath() + "/company/listings?success=Job+updated+successfully");

        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "UPDATE_JOB", e.getMessage(), currentUserId);
            req.setAttribute("error", "Failed to update job. Please try again.");
            req.getRequestDispatcher("/views/company/edit-job.jsp").forward(req, resp);
        }
    }
}
