package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.entity.Company;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.CompanyRegistrationService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Admin servlet for managing company registration approvals.
 * Only accessible by SYSTEM_ADMIN users.
 */
@WebServlet(urlPatterns = {"/admin/companies/pending", "/admin/companies/approve", "/admin/companies/reject"})
public class AdminCompanyApprovalServlet extends HttpServlet {

    private static final String SERVLET_NAME = "AdminCompanyApprovalServlet";

    @Inject
    private CompanyRegistrationService companyRegistrationService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Check authentication and authorization
        String userId = checkAuthAndGetUserId(req, resp);
        if (userId == null) return;

        String path = req.getServletPath();

        if ("/admin/companies/pending".equals(path)) {
            // Display pending companies
            List<Company> pendingCompanies = companyRegistrationService.getPendingCompanies();
            req.setAttribute("pendingCompanies", pendingCompanies);
            req.getRequestDispatcher("/views/admin/pending-companies.jsp").forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Check authentication and authorization
        String adminId = checkAuthAndGetUserId(req, resp);
        if (adminId == null) return;

        String path = req.getServletPath();
        String companyId = req.getParameter("companyId");

        if (companyId == null || companyId.trim().isEmpty()) {
            req.setAttribute("error", "Company ID is required");
            resp.sendRedirect(req.getContextPath() + "/admin/companies/pending");
            return;
        }

        try {
            if ("/admin/companies/approve".equals(path)) {
                // Approve company
                String baseUrl = getBaseUrl(req);
                boolean approved = companyRegistrationService.approveCompany(companyId, adminId, baseUrl);
                if (approved) {
                    req.getSession().setAttribute("successMessage", "Company approved successfully!");
                    MainLogger.logUserAction(SERVLET_NAME, adminId, "APPROVE_COMPANY: " + companyId);
                }
            } else if ("/admin/companies/reject".equals(path)) {
                // Reject company
                String reason = req.getParameter("reason");
                boolean rejected = companyRegistrationService.rejectCompany(companyId, adminId, reason);
                if (rejected) {
                    req.getSession().setAttribute("successMessage", "Company rejected successfully!");
                    MainLogger.logUserAction(SERVLET_NAME, adminId, "REJECT_COMPANY: " + companyId);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/admin/companies/pending");

        } catch (IllegalArgumentException e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "COMPANY_ACTION", e.getMessage(), adminId);
            req.getSession().setAttribute("errorMessage", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/companies/pending");
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "COMPANY_ACTION", e.getMessage(), adminId);
            req.getSession().setAttribute("errorMessage", "An error occurred. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/admin/companies/pending");
        }
    }

    /**
     * Checks if user is authenticated and is a SYSTEM_ADMIN.
     * Returns userId if authorized, null if not.
     */
    private String checkAuthAndGetUserId(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String userId = (String) req.getSession().getAttribute("userId");
        Role userRole = (Role) req.getSession().getAttribute("role");

        if (userId == null && req.getUserPrincipal() != null) {
            userId = req.getUserPrincipal().getName();
            req.getSession().setAttribute("userId", userId);
        }

        if (userId == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return null;
        }

        if (userRole != Role.SYSTEM_ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied - System Admin role required");
            return null;
        }

        return userId;
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
