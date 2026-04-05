package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.logging.BusinessLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/internships")
public class InternshipsServlet extends HttpServlet {
    
    private static final String SERVLET_NAME = "InternshipsServlet";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String user = req.getRemoteUser() != null ? req.getRemoteUser() : "anonymous";
        boolean isAuth = req.getRemoteUser() != null;
        
        BusinessLogger.logPageView(SERVLET_NAME, "internships.jsp", user, isAuth);
        
        try {
            req.getRequestDispatcher("/views/internships.jsp").forward(req, resp);
        } catch (Exception e) {
            BusinessLogger.logError(SERVLET_NAME, "VIEW_INTERNSHIPS", 
                e.getMessage(), user);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load internships page");
        }
    }
}