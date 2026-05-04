package com.jakartaee.jobfinder.servlet.dashboard;

import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.AdminService;
import com.jakartaee.jobfinder.services.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servlet for displaying the admin dashboard.
 * Shows system statistics, recent activities, and admin information.
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private static final String SERVLET_NAME = "AdminDashboardServlet";
    
    @Inject
    private AuthService authService;
    
    @Inject
    private AdminService adminService;

    /**
     * Displays the admin dashboard with system statistics and recent activities.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response for rendering the dashboard view
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = authService.requireSystemAdmin(req, resp, SERVLET_NAME);
        if (user == null) {
            return;
        }

        String currentUserId = user.getId();
        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_ADMIN_DASHBOARD");

        try {
            // User already loaded from role check above
            String actualUsername = user.getName();

            // Set attributes for JSP
            req.setAttribute("currentUser", actualUsername);
            req.setAttribute("userRole", user.getRole().name());
            req.setAttribute("userId", currentUserId);
            req.setAttribute("adminId", currentUserId);

            // Set authentication attributes for navbar
            req.setAttribute("isAuthenticated", true);
            req.setAttribute("role", user.getRole().name());

            // Set admin dashboard statistics from database
            Map<String, Object> stats = adminService.getAdminDashboardStats();
            req.setAttribute("adminStats", stats);

            // Set recent system activities from database
            List<Map<String, String>> adminActivities = adminService.getRecentSystemActivities(10);
            req.setAttribute("adminActivities", adminActivities);

            req.getRequestDispatcher("/views/admin/admin-dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_DASHBOARD",
                    e.getMessage(), currentUserId);
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load admin dashboard");
        }
    }
}
