package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.dao.CompanyDAO;
import com.jakartaee.jobfinder.dto.PaginationDTO;
import com.jakartaee.jobfinder.models.Company;
import com.jakartaee.jobfinder.logging.BusinessLogger;
import com.jakartaee.jobfinder.utils.PaginationUtil;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/companies")
public class CompaniesServlet extends HttpServlet {
    
    private static final String SERVLET_NAME = "CompaniesServlet";
    
    @Inject
    private CompanyDAO companyDAO;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String currentUser = "anonymous";
        boolean isAuthenticated = false;
        
        // Try to get authenticated user info, but don't fail if not authenticated
        try {
            if (req.getUserPrincipal() != null) {
                currentUser = req.getUserPrincipal().getName();
                req.setAttribute("currentUser", currentUser);
                req.setAttribute("isAdmin", req.isUserInRole("SYSTEM_ADMIN"));
                isAuthenticated = true;
            }
        } catch (Exception e) {
            // User is not authenticated, proceed as anonymous
        }
        
        BusinessLogger.logPageView(SERVLET_NAME, "companies.jsp", currentUser, isAuthenticated);
        
        try {
            // Fetch all companies from database
            List<Company> companies = companyDAO.findAll();
            
            // Pagination
            int page = PaginationUtil.parsePageParameter(req.getParameter("page"));
            PaginationDTO<Company> pagination = PaginationUtil.paginate(companies, page, 10);
            
            BusinessLogger.logDataAccess(SERVLET_NAME, "Company", pagination.getTotalItems(), currentUser);
            
            req.setAttribute("companies", pagination.getItems());
            req.setAttribute("pagination", pagination);
            
            // Forward to companies.jsp
            req.getRequestDispatcher("/views/companies.jsp").forward(req, resp);
            
        } catch (Exception e) {
            BusinessLogger.logError(SERVLET_NAME, "LOAD_COMPANIES", 
                e.getMessage(), currentUser);
            
            // Set empty list to prevent errors
            req.setAttribute("companies", new ArrayList<>());
            req.setAttribute("error", "Unable to load companies at this time. Please try again later.");
            req.getRequestDispatcher("/views/companies.jsp").forward(req, resp);
        }
    }
}

