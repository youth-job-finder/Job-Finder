package com.jakartaee.jobfinder.servlet.admin;

import com.jakartaee.jobfinder.dao.JobDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.Job;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Admin servlet for managing jobs.
 * Only accessible by SYSTEM_ADMIN users.
 */
@WebServlet("/admin/jobs")
public class AdminJobsServlet extends HttpServlet {

    private static final String SERVLET_NAME = "AdminJobsServlet";

    @Inject
    private JobDAO jobDAO;

    @Inject
    private UserDAO userDAO;

    /**
     * Displays all jobs for management with filtering support.
     *
     * @param req  the HTTP request, may contain search and company filter parameters
     * @param resp the HTTP response for rendering the jobs view
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Authentication check
        String currentUserId = getCurrentUser(req);
        if (currentUserId == null || currentUserId.equals("unknown")) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        // Authorization check - must be SYSTEM_ADMIN
        User currentUser = userDAO.findById(currentUserId);
        if (currentUser == null || currentUser.getRole() != Role.SYSTEM_ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "System Admin access required");
            return;
        }

        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_JOBS_MANAGEMENT");

        try {
            // Get all jobs
            List<Job> jobs = jobDAO.findAll();

            // Get filter parameters
            String searchQuery = req.getParameter("search");
            String companyFilter = req.getParameter("company");

            // Filter jobs if needed
            if (searchQuery != null && !searchQuery.isEmpty()) {
                String lowerQuery = searchQuery.toLowerCase();
                jobs = jobs.stream()
                    .filter(j -> j.getJobTitle().toLowerCase().contains(lowerQuery) ||
                                j.getJobDescription().toLowerCase().contains(lowerQuery))
                    .toList();
            }

            req.setAttribute("jobs", jobs);
            req.setAttribute("jobCount", jobs.size());
            req.setAttribute("adminId", currentUserId);
            req.setAttribute("currentUri", req.getRequestURI());

            req.getRequestDispatcher("/views/admin/jobs.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_JOBS", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load jobs");
        }
    }

    /**
     * Processes job management actions.
     * Supports actions: delete.
     *
     * @param req  the HTTP request containing action and jobId parameters
     * @param resp the HTTP response for redirecting after action
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String currentUserId = getCurrentUser(req);
        if (currentUserId == null || currentUserId.equals("unknown")) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        User currentUser = userDAO.findById(currentUserId);
        if (currentUser == null || currentUser.getRole() != Role.SYSTEM_ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "System Admin access required");
            return;
        }

        String action = req.getParameter("action");
        String jobId = req.getParameter("jobId");

        try {
            switch (action) {
                case "delete":
                    deleteJob(jobId, currentUserId, req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            }
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "JOB_ACTION", e.getMessage(), currentUserId);
            req.getSession().setAttribute("errorMessage", "Action failed: " + e.getMessage());
            resp.sendRedirect(req.getRequestURI());
        }
    }

    private void deleteJob(String jobId, String adminId, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Job job = jobDAO.findById(jobId);
        if (job == null) {
            req.getSession().setAttribute("errorMessage", "Job not found");
            resp.sendRedirect(req.getRequestURI());
            return;
        }

        jobDAO.delete(jobId);
        MainLogger.logUserAction(SERVLET_NAME, adminId, "DELETE_JOB: " + jobId);
        req.getSession().setAttribute("successMessage", "Job deleted successfully");
        resp.sendRedirect(req.getRequestURI());
    }

    private String getCurrentUser(HttpServletRequest req) {
        String userId = (String) req.getSession().getAttribute("userId");
        if (userId == null && req.getUserPrincipal() != null) {
            userId = req.getUserPrincipal().getName();
        }
        return userId != null ? userId : "unknown";
    }
}
