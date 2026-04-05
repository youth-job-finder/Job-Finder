package com.jakartaee.jobfinder.servlet.admin;

import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Admin servlet for system settings.
 * Only accessible by SYSTEM_ADMIN users.
 */
@WebServlet("/admin/settings")
public class AdminSettingsServlet extends HttpServlet {

    private static final String SERVLET_NAME = "AdminSettingsServlet";

    @Inject
    private UserDAO userDAO;

    // In-memory settings (in production, these would be stored in database)
    private static final Map<String, String> systemSettings = new HashMap<>();

    static {
        systemSettings.put("siteName", "JobFinder");
        systemSettings.put("maxUploadSize", "10");
        systemSettings.put("enableEmailVerification", "true");
        systemSettings.put("enableUrlVerification", "true");
        systemSettings.put("defaultJobExpiryDays", "30");
        systemSettings.put("maintenanceMode", "false");
    }

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

        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_SETTINGS");

        req.setAttribute("settings", systemSettings);
        req.setAttribute("adminId", currentUserId);
        req.setAttribute("currentUri", req.getRequestURI());

        req.getRequestDispatcher("/views/admin/settings.jsp").forward(req, resp);
    }

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

        try {
            if ("update".equals(action)) {
                // Update settings
                systemSettings.put("siteName", req.getParameter("siteName"));
                systemSettings.put("maxUploadSize", req.getParameter("maxUploadSize"));
                systemSettings.put("enableEmailVerification", req.getParameter("enableEmailVerification") != null ? "true" : "false");
                systemSettings.put("enableUrlVerification", req.getParameter("enableUrlVerification") != null ? "true" : "false");
                systemSettings.put("defaultJobExpiryDays", req.getParameter("defaultJobExpiryDays"));
                systemSettings.put("maintenanceMode", req.getParameter("maintenanceMode") != null ? "true" : "false");

                MainLogger.logUserAction(SERVLET_NAME, currentUserId, "UPDATE_SETTINGS");
                req.getSession().setAttribute("successMessage", "Settings updated successfully");
            }

            resp.sendRedirect(req.getRequestURI());

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "UPDATE_SETTINGS", e.getMessage(), currentUserId);
            req.getSession().setAttribute("errorMessage", "Failed to update settings");
            resp.sendRedirect(req.getRequestURI());
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
