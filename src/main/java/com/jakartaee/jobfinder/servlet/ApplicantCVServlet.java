package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.dao.CVDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.CV;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet("/applicant/cv")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 10 * 1024 * 1024,  // 10MB
        maxRequestSize = 20 * 1024 * 1024 // 20MB
)
public class ApplicantCVServlet extends HttpServlet {

    private static final String SERVLET_NAME = "ApplicantCVServlet";

    @Inject
    private UserDAO userDAO;

    @Inject
    private CVDAO cvDAO;

    // Directory to store uploaded CVs - external persistent storage
    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/jobfinder-uploads/cvs";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException{
        // Enforce authentication - no redirects, return 401 if not authenticated
        String userId = (String) request.getSession().getAttribute("userId");
        if (userId == null && request.getUserPrincipal() != null) {
            userId = request.getUserPrincipal().getName();
            request.getSession().setAttribute("userId", userId);
        }
        
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        // Check authorization - must have APPLICANT role (from database)
        User user = userDAO.findById(userId);
        if (user == null || user.getRole() != Role.APPLICANT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied - Applicant role required");
            return;
        }
        
        // Check email verification
        if (!user.getEmailVerified()) {
            MainLogger.logError(SERVLET_NAME, "Unverified user attempted to access CV: " + userId);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Email verification required. Please verify your email before managing your CV.");
            return;
        }
        
        // Set authentication attributes for navbar
        request.setAttribute("isAuthenticated", true);
        request.setAttribute("userRole", "APPLICANT");
        
        // Load CV info from database
        loadCVInfo(request, user);
        
        request.getRequestDispatcher("/views/cv.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Enforce authentication - no redirects, return 401 if not authenticated
        String userId = (String) req.getSession().getAttribute("userId");
        if (userId == null && req.getUserPrincipal() != null) {
            userId = req.getUserPrincipal().getName();
            req.getSession().setAttribute("userId", userId);
        }
        
        if (userId == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        // Check authorization - must have APPLICANT role (from database)
        User user = userDAO.findById(userId);
        if (user == null || user.getRole() != Role.APPLICANT) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied - Applicant role required");
            return;
        }

        // Check email verification
        if (!user.getEmailVerified()) {
            MainLogger.logError(SERVLET_NAME, "Unverified user attempted CV operation: " + userId);
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Email verification required. Please verify your email before managing your CV.");
            return;
        }

        String action = req.getParameter("action");
        
        if ("download".equals(action)) {
            handleDownload(req, resp, user);
        } else if ("delete".equals(action)) {
            handleDelete(req, resp, user);
        } else {
            handleUpload(req, resp, user);
        }
    }

    private void handleUpload(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        MainLogger.logUserAction(SERVLET_NAME, user.getId(), "UPLOAD_CV_ATTEMPT");

        try {
            Part cvPart = req.getPart("cvFile");
            if (cvPart == null || cvPart.getSize() == 0) {
                req.setAttribute("errorMessage", "No file selected. Please choose a CV file.");
                loadCVInfo(req, user);
                req.getRequestDispatcher("/views/cv.jsp").forward(req, resp);
                return;
            }

            String fileName = Paths.get(cvPart.getSubmittedFileName()).getFileName().toString();

            // Ensure upload directory exists
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save file to server
            File file = new File(uploadDir, fileName);
            try (InputStream input = cvPart.getInputStream()) {
                Files.copy(input, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            // Update CV record in DB
            CV cv = cvDAO.findByUser(user).orElse(new CV(user, fileName, file.getAbsolutePath()));
            cv.setFileName(fileName);
            cv.setFilePath(file.getAbsolutePath());
            cvDAO.saveOrUpdate(cv);

            req.getSession().setAttribute("cvFileName", fileName);
            req.setAttribute("successMessage", "CV uploaded successfully!");
            MainLogger.logServiceOperation(SERVLET_NAME, "UPLOAD_CV", true, "File: " + fileName);
            
            // Set authentication attributes for navbar
            req.setAttribute("isAuthenticated", true);
            req.setAttribute("userRole", "APPLICANT");

        } catch (Exception e) {
            req.setAttribute("errorMessage", "Failed to upload CV. Please try again.");
            MainLogger.logAuthenticationError(SERVLET_NAME, "UPLOAD_CV", e.getMessage(), user.getId());
        }

        loadCVInfo(req, user);
        req.getRequestDispatcher("/views/cv.jsp").forward(req, resp);
    }

    private void handleDownload(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        MainLogger.logUserAction(SERVLET_NAME, user.getId(), "DOWNLOAD_CV");
        
        try {
            CV cv = cvDAO.findByUser(user).orElse(null);
            if (cv == null || cv.getFilePath() == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "CV not found");
                return;
            }

            File file = new File(cv.getFilePath());
            if (!file.exists()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "CV file not found on server");
                return;
            }

            // Set response headers
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + cv.getFileName() + "\"");
            resp.setContentLength((int) file.length());

            // Stream file to response
            Files.copy(file.toPath(), resp.getOutputStream());
            resp.getOutputStream().flush();
            
            MainLogger.logServiceOperation(SERVLET_NAME, "DOWNLOAD_CV", true, "File: " + cv.getFileName());
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "DOWNLOAD_CV", e.getMessage(), user.getId());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to download CV");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        MainLogger.logUserAction(SERVLET_NAME, user.getId(), "DELETE_CV");
        
        try {
            CV cv = cvDAO.findByUser(user).orElse(null);
            if (cv != null && cv.getFilePath() != null) {
                // Delete file from server
                File file = new File(cv.getFilePath());
                if (file.exists()) {
                    file.delete();
                }
                
                // Delete from database
                cvDAO.delete(cv);
                
                // Remove from session
                req.getSession().removeAttribute("cvFileName");
                
                req.setAttribute("successMessage", "CV deleted successfully!");
                MainLogger.logServiceOperation(SERVLET_NAME, "DELETE_CV", true, "File: " + cv.getFileName());
            } else {
                req.setAttribute("errorMessage", "No CV found to delete.");
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Failed to delete CV. Please try again.");
            MainLogger.logAuthenticationError(SERVLET_NAME, "DELETE_CV", e.getMessage(), user.getId());
        }

        loadCVInfo(req, user);
        req.setAttribute("isAuthenticated", true);
        req.setAttribute("userRole", "APPLICANT");
        req.getRequestDispatcher("/views/cv.jsp").forward(req, resp);
    }

    private void loadCVInfo(HttpServletRequest req, User user) {
        // Load CV info from database
        CV cv = cvDAO.findByUser(user).orElse(null);
        if (cv != null) {
            req.setAttribute("cvFileName", cv.getFileName());
            req.setAttribute("cvUploadedAt", cv.getUploadedAt());
            req.getSession().setAttribute("cvFileName", cv.getFileName());
        } else {
            req.removeAttribute("cvFileName");
            req.getSession().removeAttribute("cvFileName");
        }
    }
}
