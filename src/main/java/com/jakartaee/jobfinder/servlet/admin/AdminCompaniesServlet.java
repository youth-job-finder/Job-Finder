package com.jakartaee.jobfinder.servlet.admin;

import com.jakartaee.jobfinder.dao.CompanyDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.dto.PaginationDTO;
import com.jakartaee.jobfinder.models.Company;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.models.status.Status;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.CompanyRegistrationService;
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
 * Admin servlet for managing companies.
 * Only accessible by SYSTEM_ADMIN users.
 */
@WebServlet(urlPatterns = {"/admin/companies", "/admin/companies/delete"})
public class AdminCompaniesServlet extends HttpServlet {

    private static final String SERVLET_NAME = "AdminCompaniesServlet";

    @Inject
    private CompanyDAO companyDAO;

    @Inject
    private CompanyRegistrationService companyService;

    @Inject
    private UserDAO userDAO;

    /**
     * Displays all companies for management.
     * Supports filtering by status and search query.
     *
     * @param req  the HTTP request, may contain status filter and search parameters
     * @param resp the HTTP response for rendering the companies view
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

        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_COMPANIES_MANAGEMENT");

        try {
            // Get all companies
            List<Company> companies = companyDAO.findAll();

            // Get filter parameters
            String statusFilter = req.getParameter("status");
            String searchQuery = req.getParameter("search");

            // Filter companies if needed
            if (statusFilter != null && !statusFilter.isEmpty()) {
                Status status = Status.valueOf(statusFilter);
                companies = companies.stream()
                    .filter(c -> c.getStatus() == status)
                    .toList();
            }

            if (searchQuery != null && !searchQuery.isEmpty()) {
                String lowerQuery = searchQuery.toLowerCase();
                companies = companies.stream()
                    .filter(c -> c.getName().toLowerCase().contains(lowerQuery) ||
                                c.getEmail().toLowerCase().contains(lowerQuery) ||
                                c.getRegistrationNumber().toLowerCase().contains(lowerQuery))
                    .toList();
            }

            // Count by status
            long pendingCount = companies.stream().filter(c -> c.getStatus() == Status.PENDING).count();
            long approvedCount = companies.stream().filter(c -> c.getStatus() == Status.APPROVED).count();
            long rejectedCount = companies.stream().filter(c -> c.getStatus() == Status.REJECTED).count();

            // Pagination
            int page = PaginationUtil.parsePageParameter(req.getParameter("page"));
            PaginationDTO<Company> pagination = PaginationUtil.paginate(companies, page, 10);
            
            req.setAttribute("companies", pagination.getItems());
            req.setAttribute("pagination", pagination);
            req.setAttribute("companyCount", companies.size());
            req.setAttribute("pendingCount", pendingCount);
            req.setAttribute("approvedCount", approvedCount);
            req.setAttribute("rejectedCount", rejectedCount);
            req.setAttribute("adminId", currentUserId);
            req.setAttribute("currentUri", req.getRequestURI());

            req.getRequestDispatcher("/views/admin/companies.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_COMPANIES", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load companies");
        }
    }

    /**
     * Processes company management actions.
     * Supports actions: approve, reject, delete.
     *
     * @param req  the HTTP request containing action and company data
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
        String companyId = req.getParameter("companyId");

        try {
            switch (action) {
                case "delete":
                    deleteCompany(companyId, currentUserId, req, resp);
                    break;
                case "approve":
                    approveCompany(companyId, currentUserId, req, resp);
                    break;
                case "reject":
                    rejectCompany(companyId, req.getParameter("reason"), currentUserId, req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            }
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "COMPANY_ACTION", e.getMessage(), currentUserId);
            req.getSession().setAttribute("errorMessage", "Action failed: " + e.getMessage());
            resp.sendRedirect(req.getRequestURI());
        }
    }

    private void deleteCompany(String companyId, String adminId, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Company company = companyDAO.findById(companyId);
        if (company == null) {
            req.getSession().setAttribute("errorMessage", "Company not found");
            resp.sendRedirect(req.getRequestURI());
            return;
        }

        companyDAO.delete(companyId);
        MainLogger.logUserAction(SERVLET_NAME, adminId, "DELETE_COMPANY: " + companyId);
        req.getSession().setAttribute("successMessage", "Company and associated admin deleted successfully");
        resp.sendRedirect(req.getRequestURI());
    }

    private void approveCompany(String companyId, String adminId, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String baseUrl = getBaseUrl(req);
        boolean approved = companyService.approveCompany(companyId, adminId, baseUrl);
        if (approved) {
            MainLogger.logUserAction(SERVLET_NAME, adminId, "APPROVE_COMPANY: " + companyId);
            req.getSession().setAttribute("successMessage", "Company approved successfully");
        }
        resp.sendRedirect(req.getRequestURI());
    }

    private void rejectCompany(String companyId, String reason, String adminId, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        boolean rejected = companyService.rejectCompany(companyId, adminId, reason != null ? reason : "Rejected by admin");
        if (rejected) {
            MainLogger.logUserAction(SERVLET_NAME, adminId, "REJECT_COMPANY: " + companyId);
            req.getSession().setAttribute("successMessage", "Company rejected successfully");
        }
        resp.sendRedirect(req.getRequestURI());
    }

    private String getCurrentUser(HttpServletRequest req) {
        String userId = (String) req.getSession().getAttribute("userId");
        if (userId == null && req.getUserPrincipal() != null) {
            userId = req.getUserPrincipal().getName();
        }
        return userId != null ? userId : "unknown";
    }
    
    private String getBaseUrl(HttpServletRequest req) {
        String scheme = req.getScheme();
        String serverName = req.getServerName();
        int serverPort = req.getServerPort();
        String contextPath = req.getContextPath();
        
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(scheme).append("://").append(serverName);
        if (serverPort != 80 && serverPort != 443) {
            baseUrl.append(":").append(serverPort);
        }
        baseUrl.append(contextPath);
        return baseUrl.toString();
    }
}
