
# 📂 JWT-Secured CV Management System (Upload, Download, Dashboard, Delete)

This guide provides a complete workflow for managing CVs in a **Servlet-based application** with **JWT authentication** and **MySQL metadata storage**.

---

## 🔄 Flow Overview

1. **Login Servlet**
    - Issues JWT with `userId` and `role` claims.

2. **JwtFilter**
    - Validates JWT before secure endpoints.
    - Extracts claims and attaches them to the request.

3. **UploadCVServlet**
    - Saves CV file locally.
    - Inserts metadata into MySQL.

4. **DashboardCVServlet**
    - Lists CVs for the authenticated user.
    - Provides **Download** and **Delete** actions.

5. **DownloadCVServlet**
    - Validates JWT and ownership.
    - Streams CV file securely.

6. **DeleteCVServlet**
    - Validates JWT and ownership.
    - Deletes CV file from local file system.
    - Removes metadata entry from MySQL.

---

## 🗄️ Database Schema (MySQL)

```sql
CREATE TABLE cv_files (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🚀 UploadCVServlet (JWT + DB Integration)

```java
@WebServlet("/uploadCV")
@MultipartConfig
public class UploadCVServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "/var/app/uploads/cv";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
            return;
        }

        for (Part part : request.getParts()) {
            String fileName = extractFileName(part);
            if (fileName != null && !fileName.isEmpty()) {
                String uniqueFileName = userId + "_" + System.currentTimeMillis() + "_" + fileName;
                String filePath = UPLOAD_DIR + File.separator + uniqueFileName;

                part.write(filePath);

                try (Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/yourdb", "user", "password")) {

                    String sql = "INSERT INTO cv_files (user_id, file_name, file_path) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, userId);
                    stmt.setString(2, uniqueFileName);
                    stmt.setString(3, filePath);
                    stmt.executeUpdate();

                } catch (SQLException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error: " + e.getMessage());
                    return;
                }

                response.getWriter().println("CV uploaded successfully: " + uniqueFileName);
            }
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return null;
    }
}
```

---

## 🚀 DashboardCVServlet (JWT + DB Integration)

```java
@WebServlet("/dashboardCV")
public class DashboardCVServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>CV Dashboard</title></head><body>");
        out.println("<h2>Your Uploaded CVs</h2>");
        out.println("<table border='1'>");
        out.println("<tr><th>ID</th><th>Filename</th><th>Upload Date</th><th>Actions</th></tr>");

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/yourdb", "user", "password")) {

            String sql = "SELECT id, file_name, upload_date FROM cv_files WHERE user_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int cvId = rs.getInt("id");
                String fileName = rs.getString("file_name");
                String uploadDate = rs.getString("upload_date");

                out.println("<tr>");
                out.println("<td>" + cvId + "</td>");
                out.println("<td>" + fileName + "</td>");
                out.println("<td>" + uploadDate + "</td>");
                out.println("<td>"
                        + "<a href='downloadCV?cvId=" + cvId + "'>Download</a> | "
                        + "<form action='deleteCV' method='post' style='display:inline;'>"
                        + "<input type='hidden' name='cvId' value='" + cvId + "'/>"
                        + "<button type='submit'>Delete</button></form>"
                        + "</td>");
                out.println("</tr>");
            }

        } catch (SQLException e) {
            out.println("<p>Error loading CVs: " + e.getMessage() + "</p>");
        }

        out.println("</table>");
        out.println("</body></html>");
    }
}
```

---

## 🚀 DownloadCVServlet (JWT Ownership Check)

```java
@WebServlet("/downloadCV")
public class DownloadCVServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer userId = (Integer) request.getAttribute("userId");
        int cvId = Integer.parseInt(request.getParameter("cvId"));

        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/yourdb", "user", "password")) {

            String sql = "SELECT file_name, file_path, user_id FROM cv_files WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cvId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int ownerId = rs.getInt("user_id");
                if (ownerId != userId) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                    return;
                }

                String fileName = rs.getString("file_name");
                String filePath = rs.getString("file_path");

                File file = new File(filePath);
                if (!file.exists()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                    return;
                }

                response.setContentType(getServletContext().getMimeType(filePath));
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                response.setContentLength((int) file.length());

                try (FileInputStream inStream = new FileInputStream(file);
                     OutputStream outStream = response.getOutputStream()) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "CV not found");
            }

        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error: " + e.getMessage());
        }
    }
}
```

---

## 🚀 DeleteCVServlet (JWT + DB Update)

```java
@WebServlet("/deleteCV")
public class DeleteCVServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer userId = (Integer) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        int cvId = Integer.parseInt(request.getParameter("cvId"));

        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/yourdb", "user", "password")) {

            String sql = "SELECT file_path, user_id FROM cv_files WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cvId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int ownerId = rs.getInt("user_id");
                String filePath = rs.getString("file_path");

                if (ownerId != userId && !"ADMIN".equals(role)) {
                    response.sendError(HttpServletResponse.SC
