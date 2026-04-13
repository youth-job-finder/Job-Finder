package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.dao.SavedJobDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.Job;
import com.jakartaee.jobfinder.entity.SavedJob;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.logging.BusinessLogger;
import com.jakartaee.jobfinder.services.JobService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet("/jobs")
public class JobsServlet extends HttpServlet {

    private static final String SERVLET_NAME = "JobsServlet";

    @Inject
    private JobService jobService;

    @Inject
    private UserDAO userDAO;

    @Inject
    private SavedJobDAO savedJobDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String remoteUser = req.getRemoteUser();
        String username = remoteUser != null ? remoteUser : "anonymous";
        boolean isAuthenticated = remoteUser != null;

        BusinessLogger.logPageView(SERVLET_NAME, "jobs.jsp", username, isAuthenticated);

        try {
            // Check authentication status for navbar/footer selection
            String userId = (String) req.getSession().getAttribute("userId");

            // Also check container authentication (for users who logged in via form auth)
            if (userId == null) {
                remoteUser = req.getRemoteUser();
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

            isAuthenticated = userId != null;
            req.setAttribute("isAuthenticated", isAuthenticated);

            String userRole = "";
            if (isAuthenticated) {
                Object roleObj = req.getSession().getAttribute("role");
                userRole = roleObj != null ? roleObj.toString() : "";
                req.setAttribute("userRole", userRole);
            }

            // Handle filter parameter
            String filter = req.getParameter("filter");
            List<Job> jobs;

            if ("saved".equals(filter) && isAuthenticated && "APPLICANT".equals(userRole)) {
                // Get saved jobs for the applicant
                User user = userDAO.findById(userId);
                if (user != null) {
                    List<SavedJob> savedJobs = savedJobDAO.findByApplicant(user);
                    jobs = savedJobs.stream()
                            .map(SavedJob::getJob)
                            .collect(Collectors.toList());
                } else {
                    jobs = List.of();
                }
            } else if ("remote".equals(filter)) {
                jobs = jobService.getRemoteJobs();
            } else {
                // Get all jobs
                jobs = jobService.getAllJobs();
            }

            BusinessLogger.logDataAccess(SERVLET_NAME, "Job", jobs.size(), username);

            req.setAttribute("jobs", jobs);

            // If applicant is logged in, get their saved job IDs for the save/unsave buttons
            if (isAuthenticated && "APPLICANT".equals(userRole)) {
                User user = userDAO.findById(userId);
                if (user != null) {
                    List<SavedJob> savedJobs = savedJobDAO.findByApplicant(user);
                    Set<String> savedJobIds = savedJobs.stream()
                            .map(savedJob -> savedJob.getJob().getId())
                            .collect(Collectors.toSet());
                    req.setAttribute("savedJobIds", savedJobIds);
                }
            }

            req.getRequestDispatcher("/views/jobs.jsp").forward(req, resp);

        } catch (Exception e) {
            BusinessLogger.logError(SERVLET_NAME, "LOAD_JOBS",
                    e.getMessage(), username);

            req.setAttribute("jobs", List.of());
            req.setAttribute("error", "Unable to load jobs at this time. Please try again later.");
            req.getRequestDispatcher("/views/jobs.jsp").forward(req, resp);
        }
    }
}
