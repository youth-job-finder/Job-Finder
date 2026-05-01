package com.jakartaee.jobfinder.servlet.admin;

import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.dto.PaginationDTO;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.AuthService;
import com.jakartaee.jobfinder.utils.PaginationUtil;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Admin servlet for managing users.
 * Only accessible by SYSTEM_ADMIN users.
 */
@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {

    private static final String SERVLET_NAME = "AdminUsersServlet";

    @Inject
    private AuthService authService;

    @Inject
    private UserDAO userDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User currentUser = authService.requireSystemAdmin(req, resp, SERVLET_NAME);
        if (currentUser == null) {
            return;
        }

        String currentUserId = currentUser.getId();
        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_USERS_MANAGEMENT");

        try {
            // Get all users
            List<User> users = userDAO.findAll();

            // Get filter parameters
            String roleFilter = req.getParameter("role");
            String searchQuery = req.getParameter("search");

            // Filter users if needed
            if (roleFilter != null && !roleFilter.isEmpty()) {
                users = users.stream()
                    .filter(u -> u.getRole().name().equals(roleFilter))
                    .toList();
            }

            if (searchQuery != null && !searchQuery.isEmpty()) {
                String lowerQuery = searchQuery.toLowerCase();
                users = users.stream()
                    .filter(u -> u.getName().toLowerCase().contains(lowerQuery) ||
                                u.getEmail().toLowerCase().contains(lowerQuery))
                    .toList();
            }

            // Pagination
            int page = PaginationUtil.parsePageParameter(req.getParameter("page"));
            PaginationDTO<User> pagination = PaginationUtil.paginate(users, page, 10);
            
            req.setAttribute("users", pagination.getItems());
            req.setAttribute("pagination", pagination);
            req.setAttribute("userCount", users.size());
            req.setAttribute("adminId", currentUserId);
            req.setAttribute("currentUri", req.getRequestURI());

            req.getRequestDispatcher("/views/admin/users.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_USERS", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load users");
        }
    }

    /**
     * Processes user management actions.
     * Supports actions: delete, changeRole, toggleStatus.
     *
     * @param req  the HTTP request containing action and user data
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
        String userId = req.getParameter("userId");

        try {
            switch (action) {
                case "delete":
                    deleteUser(userId, currentUserId, req, resp);
                    break;
                case "changeRole":
                    changeUserRole(userId, req.getParameter("newRole"), currentUserId, req, resp);
                    break;
                case "toggleStatus":
                    toggleUserStatus(userId, currentUserId, req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            }
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "USER_ACTION", e.getMessage(), currentUserId);
            req.getSession().setAttribute("errorMessage", "Action failed: " + e.getMessage());
            resp.sendRedirect(req.getRequestURI());
        }
    }

    private void deleteUser(String userId, String adminId, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        User user = userDAO.findById(userId);
        if (user == null) {
            req.getSession().setAttribute("errorMessage", "User not found");
            resp.sendRedirect(req.getRequestURI());
            return;
        }

        // Prevent self-deletion
        if (userId.equals(adminId)) {
            req.getSession().setAttribute("errorMessage", "Cannot delete your own account");
            resp.sendRedirect(req.getRequestURI());
            return;
        }

        userDAO.delete(userId);
        MainLogger.logUserAction(SERVLET_NAME, adminId, "DELETE_USER: " + userId);
        req.getSession().setAttribute("successMessage", "User deleted successfully");
        resp.sendRedirect(req.getRequestURI());
    }

    private void changeUserRole(String userId, String newRoleStr, String adminId, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        User user = userDAO.findById(userId);
        if (user == null) {
            req.getSession().setAttribute("errorMessage", "User not found");
            resp.sendRedirect(req.getRequestURI());
            return;
        }

        Role newRole = Role.valueOf(newRoleStr);
        user.setRole(newRole);
        userDAO.update(user);

        MainLogger.logUserAction(SERVLET_NAME, adminId, "CHANGE_ROLE: " + userId + " to " + newRole);
        req.getSession().setAttribute("successMessage", "User role updated successfully");
        resp.sendRedirect(req.getRequestURI());
    }

    private void toggleUserStatus(String userId, String adminId, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        User user = userDAO.findById(userId);
        if (user == null) {
            req.getSession().setAttribute("errorMessage", "User not found");
            resp.sendRedirect(req.getRequestURI());
            return;
        }

        // Toggle email verified status as a proxy for active/inactive
        user.setEmailVerified(!user.getEmailVerified());
        userDAO.update(user);

        MainLogger.logUserAction(SERVLET_NAME, adminId, "TOGGLE_STATUS: " + userId);
        req.getSession().setAttribute("successMessage", "User status updated successfully");
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
