package com.jakartaee.jobfinder.servlet.dashboard;

import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.ApplicationService;
import com.jakartaee.jobfinder.services.CompanyService;
import com.jakartaee.jobfinder.services.JobService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet("/company/dashboard")
@RolesAllowed({"COMPANY_ADMIN"})
public class CompanyDashboardServlet extends HttpServlet {

    private static final String SERVLET_NAME = "CompanyDashboardServlet";
    
    @Inject
    private UserDAO userDAO;
    
    @Inject
    private JobService jobService;
    
    @Inject
    private ApplicationService applicationService;
    
    @Inject
    private CompanyService companyService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String currentUser = (String) req.getSession().getAttribute("userId");
        if (currentUser == null) {
            currentUser = req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : "unknown";
        }

        MainLogger.logUserAction(SERVLET_NAME, currentUser, "VIEW_COMPANY_DASHBOARD");

        try {
            // Role is enforced by @RolesAllowed
            Role userRole = Role.COMPANY_ADMIN;

            req.setAttribute("currentUser", currentUser);
            req.setAttribute("userRole", userRole.name());
            req.setAttribute("userId", currentUser);
            req.setAttribute("companyId", currentUser);
            
            // Set authentication attributes for navbar
            req.setAttribute("isAuthenticated", true);
            req.setAttribute("role", userRole.name());

            MainLogger.logSecurityEvent(SERVLET_NAME, "ROLE_CHECK",
                    "User: " + currentUser + ", Role: " + userRole);
            
            // Get current user and company
            User user = userDAO.findById(currentUser);
            MainLogger.logServiceOperation("CompanyDashboardServlet", "USER_LOOKUP", user != null, "UserID: " + currentUser + ", Found: " + (user != null));
            
            // Initialize with empty lists as defaults
            List<Map<String, String>> recentJobs = new ArrayList<>();
            List<Map<String, String>> recentActivities = new ArrayList<>();
            List<Map<String, String>> recentApplications = new ArrayList<>();
            Map<String, Integer> stats = new HashMap<>();
            
            if (user != null) {
                // Set company identifier (using email as ID)
                req.setAttribute("companyId", currentUser);
                
                // Set company dashboard statistics from database
                stats = companyService.getCompanyDashboardStats(user);
                
                // Set recent activities from database
                recentActivities = applicationService.getRecentActivitiesForCompany(user, 5);
                
                // Set company jobs
                recentJobs = jobService.getRecentJobsForCompany(user, 6);
                
                // Set recent applications
                recentApplications = applicationService.getRecentApplicationsForCompany(user, 5);
            }
            
            // Always set these attributes for the JSP
            req.setAttribute("companyStats", stats);
            req.setAttribute("companyActivities", recentActivities);
            req.setAttribute("recentJobs", recentJobs);
            req.setAttribute("recentApplications", recentApplications);
            
            MainLogger.logServiceOperation("CompanyDashboardServlet", "SET_ATTRIBUTES", true, "recentJobs count: " + recentJobs.size());

            req.getRequestDispatcher("/views/company/company-dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_DASHBOARD",
                    e.getMessage(), currentUser);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load company dashboard");
        }
    }
}
