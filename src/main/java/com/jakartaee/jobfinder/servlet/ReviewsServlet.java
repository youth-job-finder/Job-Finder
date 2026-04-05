package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.logging.BusinessLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/reviews")
public class ReviewsServlet extends HttpServlet {
    
    private static final String SERVLET_NAME = "ReviewsServlet";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String user = req.getRemoteUser() != null ? req.getRemoteUser() : "anonymous";
        boolean isAuth = req.getRemoteUser() != null;
        
        BusinessLogger.logPageView(SERVLET_NAME, "reviews.jsp", user, isAuth);
        
        try {
            req.getRequestDispatcher("/views/reviews.jsp").forward(req, resp);
        } catch (Exception e) {
            BusinessLogger.logError(SERVLET_NAME, "VIEW_REVIEWS", 
                e.getMessage(), user);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load reviews page");
        }
    }
}
