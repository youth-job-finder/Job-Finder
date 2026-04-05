package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.logging.BusinessLogger;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    
    private static final String SERVLET_NAME = "HomeServlet";

    public void init() {
        BusinessLogger.logOperation(SERVLET_NAME, "INIT", "system", "HomeServlet initialized");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String user = request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous";
        boolean isAuth = request.getRemoteUser() != null;
        
        BusinessLogger.logPageView(SERVLET_NAME, "index.jsp", user, isAuth);
        
        try {
            request.getRequestDispatcher("/views/index.jsp").forward(request, response);
        } catch (Exception e) {
            BusinessLogger.logError(SERVLET_NAME, "VIEW_HOME", 
                e.getMessage(), user);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load home page");
        }
    }
}