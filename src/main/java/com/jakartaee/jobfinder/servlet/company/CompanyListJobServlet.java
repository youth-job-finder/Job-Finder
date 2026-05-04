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
 * Servlet for listing new jobs by company administrators.
 * Handles displaying the job creation form and processing job submissions.
 */
@WebServlet("/company/list-job")
public class CompanyListJobServlet extends HttpServlet {

    private static final String SERVLET_NAME = "CompanyListJobServlet";

    @Inject
    private AuthService authService;

    @Inject
    private JobService jobService;

    /**
     * Displays the job listing form for company administrators.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response for rendering the job form view
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
        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_LIST_JOB_PAGE");

        req.setAttribute("isAuthenticated", true);
        req.setAttribute("role", Role.COMPANY_ADMIN.name());
        req.getRequestDispatcher("/views/company/list-job.jsp").forward(req, resp);
    }

    /**
     * Processes job creation submissions.
     * Validates required fields and creates a new job posting.
     *
     * @param req  the HTTP request containing job details
     * @param resp the HTTP response for redirecting after creation
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
            String jobTitle = req.getParameter("jobTitle");
            String jobDescription = req.getParameter("jobDescription");
            String jobRequirements = req.getParameter("jobRequirements");
            String physicalAddress = req.getParameter("physicalAddress");
            String salaryRange = req.getParameter("salaryRange");
            String jobType = req.getParameter("jobType");

            if (jobTitle == null || jobTitle.trim().isEmpty() ||
                jobDescription == null || jobDescription.trim().isEmpty()) {
                req.setAttribute("error", "Job title and description are required");
                req.getRequestDispatcher("/views/company/list-job.jsp").forward(req, resp);
                return;
            }

            Job job = jobService.createJob(user, jobTitle, jobDescription, jobRequirements, physicalAddress, salaryRange, jobType);

            MainLogger.logUserAction(SERVLET_NAME, currentUserId, "CREATE_JOB: " + jobTitle);
            resp.sendRedirect(req.getContextPath() + "/company/listings?success=Job+created+successfully");

        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "CREATE_JOB", e.getMessage(), currentUserId);
            req.setAttribute("error", "Failed to create job. Please try again.");
            req.getRequestDispatcher("/views/company/list-job.jsp").forward(req, resp);
        }
    }
}
