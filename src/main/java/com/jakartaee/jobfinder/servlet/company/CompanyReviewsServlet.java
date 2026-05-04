package com.jakartaee.jobfinder.servlet.company;

import com.jakartaee.jobfinder.dao.CompanyDAO;
import com.jakartaee.jobfinder.dao.ReviewDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.models.Company;
import com.jakartaee.jobfinder.models.Review;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/company/reviews")
public class CompanyReviewsServlet extends HttpServlet {

    private static final String SERVLET_NAME = "CompanyReviewsServlet";

    @Inject
    private UserDAO userDAO;

    @Inject
    private CompanyDAO companyDAO;

    @Inject
    private ReviewDAO reviewDAO;

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

        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_COMPANY_REVIEWS");

        try {
            List<Company> companies = companyDAO.findByUser(user);
            if (!companies.isEmpty()) {
                Company company = companies.get(0);
                List<Review> reviews = reviewDAO.findByCompany(company);
                req.setAttribute("reviews", reviews);
                req.setAttribute("company", company);
            }

            req.setAttribute("isAuthenticated", true);
            req.setAttribute("role", Role.COMPANY_ADMIN.name());
            req.getRequestDispatcher("/views/company/reviews.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_REVIEWS", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load reviews");
        }
    }
}
