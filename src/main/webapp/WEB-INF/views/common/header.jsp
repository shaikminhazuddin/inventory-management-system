<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} | StockMaster Inventory</title>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Custom Navy & White Theme -->
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>

<div class="app-wrapper">
    <!-- Sidebar Navigation Fragment -->
    <%@ include file="/WEB-INF/views/common/sidebar.jsp" %>

    <!-- Main Application Section -->
    <div class="app-main">
        <header class="app-navbar">
            <div class="d-flex align-items-center">
                <button class="btn btn-sm btn-outline-secondary d-lg-none me-2" id="sidebarToggle" type="button">
                    <i class="bi bi-list fs-5"></i>
                </button>
                <h1 class="h5 mb-0 fw-bold text-dark">${pageTitle}</h1>
            </div>

            <div class="d-flex align-items-center gap-3">
                <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1 rounded-pill">
                    <i class="bi bi-database-check me-1"></i> MySQL Connected
                </span>

                <div class="dropdown">
                    <button class="btn btn-sm btn-light border dropdown-toggle d-flex align-items-center gap-2"
                            type="button" data-bs-toggle="dropdown">
                        <i class="bi bi-shield-lock-fill text-primary"></i>
                        <span class="d-none d-sm-inline fw-semibold">${sessionScope.admin.username}</span>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                        <li><h6 class="dropdown-header">Signed in as Admin</h6></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/dashboard"><i class="bi bi-speedometer2 me-2"></i> Dashboard</a></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/alerts"><i class="bi bi-bell me-2"></i> Stock Alerts</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right me-2"></i> Logout</a></li>
                    </ul>
                </div>
            </div>
        </header>

        <main class="app-content">
            <!-- Global Flash Messages -->
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm" role="alert">
                    <i class="bi bi-check-circle-fill me-2"></i>
                    <c:out value="${sessionScope.successMessage}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% session.removeAttribute("successMessage"); %>
            </c:if>

            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm" role="alert">
                    <i class="bi bi-exclamation-octagon-fill me-2"></i>
                    <c:out value="${sessionScope.errorMessage}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% session.removeAttribute("errorMessage"); %>
            </c:if>

