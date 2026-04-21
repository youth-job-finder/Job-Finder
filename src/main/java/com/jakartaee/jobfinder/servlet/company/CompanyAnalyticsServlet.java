package com.jakartaee.jobfinder.servlet.company;

import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.Company;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.CompanyService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/company/analytics")
public class CompanyAnalyticsServlet extends HttpServlet {

    private static final String SERVLET_NAME = "CompanyAnalyticsServlet";

    @Inject
    private UserDAO userDAO;

    @Inject
    private CompanyService companyService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String currentUserId = (String) req.getSession().getAttribute("userId");
        if (currentUserId == null && req.getUserPrincipal() != null) {
            currentUserId = req.getUserPrincipal().getName();
        }

        if (currentUserId == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        User user = userDAO.findById(currentUserId);
        if (user == null || user.getRole() != Role.COMPANY_ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Company Admin access required");
            return;
        }

        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_COMPANY_ANALYTICS");

        try {
            Map<String, Object> analytics = companyService.getCompanyAnalytics(user);
            req.setAttribute("analytics", analytics);
            
            Company company = companyService.getCompanyByUser(user);
            req.setAttribute("company", company);

            req.setAttribute("isAuthenticated", true);
            req.setAttribute("role", Role.COMPANY_ADMIN.name());
            req.getRequestDispatcher("/views/company/analytics.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_ANALYTICS", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load analytics");
        }
    }
}
