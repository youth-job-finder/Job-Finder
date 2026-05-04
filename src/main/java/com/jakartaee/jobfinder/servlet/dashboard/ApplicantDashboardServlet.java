package com.jakartaee.jobfinder.servlet.dashboard;

import com.jakartaee.jobfinder.dao.SavedJobDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.models.SavedJob;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.ApplicantService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet("/applicant/dashboard")
public class ApplicantDashboardServlet extends HttpServlet {

    private static final String SERVLET_NAME = "ApplicantDashboardServlet";

    @Inject
    private UserDAO userDAO;

    @Inject
    private ApplicantService applicantService;

    @Inject
    private SavedJobDAO savedJobDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Enforce authentication - no redirects, return 401 if not authenticated
        String currentUserId = (String) req.getSession().getAttribute("userId");
        if (currentUserId == null && req.getUserPrincipal() != null) {
            currentUserId = req.getUserPrincipal().getName();
            req.getSession().setAttribute("userId", currentUserId);
        }
        
        if (currentUserId == null) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "UNAUTHORIZED_ACCESS",
                    "No authentication provided", "anonymous");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        // Check authorization - must have APPLICANT role (from database)
        User user = userDAO.findById(currentUserId);
        if (user == null || user.getRole() != Role.APPLICANT) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "FORBIDDEN_ACCESS",
                    "User does not have APPLICANT role", currentUserId);
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied - Applicant role required");
            return;
        }

        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_APPLICANT_DASHBOARD");

        try {
            // User already loaded from role check above
            String actualUsername = user.getName();

            // Set attributes for JSP
            req.setAttribute("currentUserEmail", user.getEmail());
            req.setAttribute("currentUserName", actualUsername);
            req.setAttribute("role", user.getRole().toString());
            
            // Set authentication attributes for navbar
            req.setAttribute("isAuthenticated", true);
            req.setAttribute("userRole", user.getRole().toString());

            // Dashboard stats
            Map<String, Integer> stats = applicantService.getDashboardStats(user);
            req.setAttribute("dashboardStats", stats);

            // Recent activities
            List<Map<String, String>> recentActivities = applicantService.getRecentActivities(user);
            req.setAttribute("recentActivities", recentActivities);

            // Recent job listings (10 most recent)
            List<Map<String, String>> recommendedJobs = applicantService.getRecommendedJobs(10);
            req.setAttribute("recommendedJobs", recommendedJobs);

            // Saved jobs - fetch actual saved jobs from database
            List<SavedJob> savedJobs = savedJobDAO.findByApplicant(user);
            req.setAttribute("savedJobs", savedJobs);
            
            req.getRequestDispatcher("/views/applicant/user-dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_DASHBOARD",
                    e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load applicant dashboard");
        }
    }
}
