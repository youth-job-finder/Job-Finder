package com.jakartaee.jobfinder.servlet.admin;

import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.AdminService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Admin servlet for viewing reports and analytics.
 * Only accessible by SYSTEM_ADMIN users.
 */
@WebServlet("/admin/reports")
public class AdminReportsServlet extends HttpServlet {

    private static final String SERVLET_NAME = "AdminReportsServlet";

    @Inject
    private UserDAO userDAO;

    @Inject
    private AdminService adminService;

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

        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_REPORTS");

        try {
            // Build comprehensive reports using AdminService
            Map<String, Object> reports = adminService.getFullReports();

            req.setAttribute("reports", reports);
            req.setAttribute("adminId", currentUserId);
            req.setAttribute("currentUri", req.getRequestURI());
            req.setAttribute("generatedAt", LocalDateTime.now());

            req.getRequestDispatcher("/views/admin/reports.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_REPORTS", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate reports");
        }
    }

    private String getCurrentUser(HttpServletRequest req) {
        String userId = (String) req.getSession().getAttribute("userId");
        if (userId == null && req.getUserPrincipal() != null) {
            userId = req.getUserPrincipal().getName();
        }
        return userId != null ? userId : "unknown";
    }
}
