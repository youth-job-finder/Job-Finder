package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/signup-options")
public class RegistrationOptionsServlet extends HttpServlet {
    
    private static final String SERVLET_NAME = "RegistrationOptionsServlet";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        MainLogger.logUserAction(SERVLET_NAME, "anonymous", "VIEW_REGISTRATION_OPTIONS_PAGE");
        
        try {
            // Forward to signup-options.jsp
            req.getRequestDispatcher("/views/signup-options.jsp").forward(req, resp);
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_REGISTRATION_OPTIONS",
                e.getMessage(), "anonymous");
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load registration options page");
        }
    }
}
