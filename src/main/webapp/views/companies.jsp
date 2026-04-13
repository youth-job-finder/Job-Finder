<%--
  Created by IntelliJ IDEA.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Top Companies - JobFinder</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/companies.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        /* 🏛️ Enterprise Navy & Gold Styling Overlays */
        :root {
            --deep-navy: #0f172a;
            --slate-navy: #1e293b;
            --darker-navy: #0b1120;
            --accent-gold: #d4af37;
            --slate-text: #94a3b8;
            --glass-white: rgba(255, 255, 255, 0.05);
        }

        body {
            background-color: var(--deep-navy) !important;
            color: #f1f5f9 !important;
            margin: 0;
            font-family: 'Segoe UI', Arial, sans-serif;
        }

        /* Header Styling */
        .page-header {
            background: linear-gradient(to bottom, #000, var(--deep-navy)) !important;
            border-bottom: 2px solid rgba(212, 175, 55, 0.1);
            padding: 60px 0;
            text-align: center;
        }
        .page-title { color: var(--accent-gold) !important; font-weight: 800 !important; font-size: 2.8rem; margin-bottom: 0.5rem; }
        .page-subtitle { color: var(--slate-text) !important; font-size: 1.1rem; }

        /* Search Section */
        .search-section { background-color: var(--deep-navy) !important; padding: 40px 0 50px !important; }
        .search-container { display: flex; flex-direction: column; align-items: center; }
        .search-box {
            background: white !important;
            border: 2px solid var(--accent-gold) !important;
            border-radius: 50px !important;
            display: flex;
            align-items: center;
            padding: 5px 20px;
            width: 100%;
            max-width: 650px;
            margin: 0 auto 20px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
        }
        .search-box .fas { color: #64748b; font-size: 1.2rem; }
        .search-input { 
            color: #0f172a !important; 
            font-weight: 500 !important; 
            border: none !important; 
            outline: none !important; 
            width: 100%;
            padding: 12px;
            background: transparent !important;
            font-size: 1rem;
        }

        /* Filter Buttons */
        .filter-buttons { display: flex; justify-content: center; gap: 15px; }
        .filter-btn {
            background: var(--glass-white) !important;
            border: 1px solid rgba(255,255,255,0.1) !important;
            color: white !important;
            border-radius: 50px !important;
            padding: 10px 25px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .filter-btn.active, .filter-btn:hover {
            background: var(--accent-gold) !important;
            color: var(--deep-navy) !important;
            border-color: var(--accent-gold) !important;
        }

        /* Table Styling */
        .companies-section { background-color: var(--darker-navy) !important; padding: 60px 0; min-height: 50vh; }
        .companies-table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0 10px;
            margin-top: 20px;
        }
        .companies-table th {
            color: var(--accent-gold);
            font-weight: 700;
            text-align: left;
            padding: 15px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        .companies-table td {
            background: var(--slate-navy);
            padding: 15px;
            color: #cbd5e1;
            border-top: 1px solid rgba(255,255,255,0.05);
            border-bottom: 1px solid rgba(255,255,255,0.05);
        }
        .companies-table td:first-child { border-left: 1px solid rgba(255,255,255,0.05); border-radius: 10px 0 0 10px; font-weight: 600; color: #fff;}
        .companies-table td:last-child { border-right: 1px solid rgba(255,255,255,0.05); border-radius: 0 10px 10px 0; }
        .company-row:hover td { background: #26354a; transition: background 0.3s ease; }
        .companies-table a { color: var(--accent-gold); text-decoration: none; }
        .companies-table a:hover { text-decoration: underline; }
    </style>
</head>

<body>

<jsp:include page="/views/components/default-navbar.jsp" />

<section class="page-header">
    <div class="container">
        <h1 class="page-title">Top Companies</h1>
        <p class="page-subtitle">Discover verified companies offering amazing opportunities</p>
    </div>
</section>

<section class="search-section">
    <div class="container">
        <div class="search-container">

            <div class="search-box">
                <i class="fas fa-search"></i>
                <input type="text" id="searchInput"
                       placeholder="Search companies by name or industry..."
                       class="search-input">
            </div>

            <div class="filter-buttons">
                <button class="filter-btn active" data-filter="all">
                    <i class="fas fa-building"></i> All Companies
                </button>

                <button class="filter-btn" data-filter="top">
                    <i class="fas fa-star"></i> Top Rated
                </button>
            </div>

        </div>
    </div>
</section>

<section class="companies-section">
    <div class="container">
        <div class="companies-grid">

            <c:choose>
                <c:when test="${not empty companies}">

                    <table class="companies-table" id="companiesTable">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Registration Number</th>
                            <th>Email</th>
                            <th>Website</th>
                            <th>Status</th>
                        </tr>
                        </thead>

                        <tbody id="companyTableBody">

                        <c:forEach var="company" items="${companies}">
                            <tr class="company-row"
                                data-name="${company.name}"
                                data-status="${company.status}">

                                <td>${company.name}</td>
                                <td>${company.registrationNumber}</td>
                                <td>${company.email}</td>
                                <td>
                                    <a href="${company.url}" target="_blank">
                                        ${company.url}
                                    </a>
                                </td>
                                <td>${company.status}</td>

                            </tr>
                        </c:forEach>

                        </tbody>
                    </table>

                </c:when>

                <c:otherwise>
                    <div style="text-align: center; padding: 50px;">
                        <i class="fas fa-building fa-3x" style="opacity: 0.2; margin-bottom: 15px; color: var(--accent-gold);"></i>
                        <p style="color: var(--slate-text);">No companies registered yet.</p>
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</section>

<jsp:include page="/views/components/footer.jsp" />

<script>
    const searchInput = document.getElementById("searchInput");
    const filterButtons = document.querySelectorAll(".filter-btn");
    const rows = document.querySelectorAll(".company-row");

    let currentFilter = "all";

    // Search functionality
    searchInput.addEventListener("keyup", function () {
        filterTable();
    });

    // Filter buttons
    filterButtons.forEach(btn => {
        btn.addEventListener("click", function () {

            // Remove active class
            filterButtons.forEach(b => b.classList.remove("active"));

            // Add active to clicked
            this.classList.add("active");

            currentFilter = this.getAttribute("data-filter");

            filterTable();
        });
    });

    function filterTable() {
        const searchValue = searchInput.value.toLowerCase();

        rows.forEach(row => {
            const name = row.getAttribute("data-name").toLowerCase();
            const status = row.getAttribute("data-status").toLowerCase();

            let matchesSearch = name.includes(searchValue);

            let matchesFilter = true;

            if (currentFilter === "top") {
                matchesFilter = (status === "top rated" || status === "approved");
            }

            if (matchesSearch && matchesFilter) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    }
</script>

</body>
</html>