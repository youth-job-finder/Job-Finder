package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.dao.SavedJobDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.models.Job;
import com.jakartaee.jobfinder.models.SavedJob;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.JobService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/job/*")
public class JobDetailServlet extends HttpServlet {

    private static final String SERVLET_NAME = "JobDetailServlet";

    @Inject
    private JobService jobService;

    @Inject
    private UserDAO userDAO;

    @Inject
    private SavedJobDAO savedJobDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required");
            return;
        }

        // Extract job ID from path (e.g., /job/123 -> 123)
        String jobId = pathInfo.substring(1);
        if (jobId.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required");
            return;
        }

        MainLogger.logInfo(SERVLET_NAME, "Viewing job details for job: " + jobId);

        try {
            Job job = jobService.getJobById(jobId);
            if (job == null) {
                MainLogger.logError(SERVLET_NAME, "Job not found: " + jobId);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Job not found");
                return;
            }

            // Check authentication status - check both session and container auth
            String userId = (String) req.getSession().getAttribute("userId");

            // Also check container authentication (for users who logged in via form auth)
            if (userId == null) {
                String remoteUser = req.getRemoteUser();
                if (remoteUser != null) {
                    userId = remoteUser;
                    req.getSession().setAttribute("userId", userId);
                    // Try to get role from user principal
                    if (req.isUserInRole("APPLICANT")) {
                        req.getSession().setAttribute("role", "APPLICANT");
                    } else if (req.isUserInRole("COMPANY_ADMIN")) {
                        req.getSession().setAttribute("role", "COMPANY_ADMIN");
                    } else if (req.isUserInRole("SYSTEM_ADMIN")) {
                        req.getSession().setAttribute("role", "SYSTEM_ADMIN");
                    }
                }
            }

            User currentUser = resolveCurrentUser(req, userId);
            if (currentUser != null) {
                userId = currentUser.getId();
                req.getSession().setAttribute("userId", userId);
                req.getSession().setAttribute("role", currentUser.getRole().name());
            }

            boolean isAuthenticated = userId != null;
            req.setAttribute("isAuthenticated", isAuthenticated);

            String userRole = "";
            if (isAuthenticated) {
                Object roleObj = req.getSession().getAttribute("role");
                userRole = roleObj != null ? roleObj.toString() : "";
                req.setAttribute("userRole", userRole);

                if (currentUser != null) {
                    req.setAttribute("user", currentUser);

                    if (currentUser.getRole() == Role.APPLICANT) {
                        populateApplicantFlags(req, currentUser, jobId);
                    }
                } else {
                    MainLogger.logInfo(SERVLET_NAME, "Authenticated session could not be resolved to a user for identifier: " + userId);
                }
            }

            req.setAttribute("job", job);
            MainLogger.logInfo(SERVLET_NAME, "Job details loaded successfully: " + job.getJobTitle());
            req.getRequestDispatcher("/views/job-detail.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logError(SERVLET_NAME, "Error loading job details for: " + jobId, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load job details");
        }
    }

    private User resolveCurrentUser(HttpServletRequest req, String userIdentifier) {
        if (userIdentifier == null || userIdentifier.trim().isEmpty()) {
            return null;
        }

        User user = userDAO.findById(userIdentifier);
        if (user != null) {
            return user;
        }

        try {
            return userDAO.findByEmail(userIdentifier).orElse(null);
        } catch (Exception e) {
            MainLogger.logError(SERVLET_NAME, "Failed to resolve user by identifier: " + userIdentifier, e);
            return null;
        }
    }

    private void populateApplicantFlags(HttpServletRequest req, User user, String jobId) {
        try {
            List<SavedJob> savedJobs = savedJobDAO.findByApplicant(user);
            boolean isSaved = savedJobs.stream()
                    .anyMatch(sj -> sj.getJob() != null && jobId.equals(sj.getJob().getId()));
            req.setAttribute("isSaved", isSaved);
        } catch (Exception e) {
            req.setAttribute("isSaved", false);
            MainLogger.logError(SERVLET_NAME, "Failed to determine saved state for job: " + jobId, e);
        }

        try {
            boolean hasApplied = jobService.hasUserApplied(jobId, user.getId());
            req.setAttribute("hasApplied", hasApplied);
        } catch (Exception e) {
            req.setAttribute("hasApplied", false);
            MainLogger.logError(SERVLET_NAME, "Failed to determine application state for job: " + jobId, e);
        }
    }
}
