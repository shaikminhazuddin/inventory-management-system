<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login | StockMaster Inventory</title>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body class="login-body">

<div class="login-card">
    <div class="text-center mb-4">
        <div class="brand-badge mx-auto mb-2" style="width: 48px; height: 48px; font-size: 1.5rem;">
            <i class="bi bi-box-seam"></i>
        </div>
        <h2 class="h4 fw-bold text-dark mb-1">StockMaster</h2>
        <p class="text-muted small">Warehouse Inventory Management System</p>
    </div>

    <!-- Error Messages -->
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show small py-2" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-1"></i>
            <c:out value="${errorMessage}"/>
            <button type="button" class="btn-close py-2" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${param.msg == 'logged_out'}">
        <div class="alert alert-info alert-dismissible fade show small py-2" role="alert">
            <i class="bi bi-info-circle-fill me-1"></i> You have been logged out successfully.
            <button type="button" class="btn-close py-2" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${param.msg == 'auth_required'}">
        <div class="alert alert-warning alert-dismissible fade show small py-2" role="alert">
            <i class="bi bi-shield-lock me-1"></i> Please sign in to access the warehouse portal.
            <button type="button" class="btn-close py-2" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="POST">
        <div class="mb-3">
            <label for="username" class="form-label">Username</label>
            <div class="input-group">
                <span class="input-group-text bg-light text-secondary"><i class="bi bi-person"></i></span>
                <input type="text" class="form-control" id="username" name="username"
                       value="${enteredUsername != null ? enteredUsername : 'admin'}" required autofocus>
            </div>
        </div>

        <div class="mb-4">
            <label for="password" class="form-label">Password</label>
            <div class="input-group">
                <span class="input-group-text bg-light text-secondary"><i class="bi bi-key"></i></span>
                <input type="password" class="form-control" id="password" name="password"
                       value="admin123" required>
            </div>
        </div>

        <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">
            <i class="bi bi-box-arrow-in-right me-1"></i> Sign In to Dashboard
        </button>
    </form>

    <div class="mt-4 pt-3 border-top text-center">
        <small class="text-muted d-block mb-1">Demo Credentials for Viva / Evaluation:</small>
        <span class="badge bg-light text-dark border px-2 py-1">admin / admin123</span>
    </div>
</div>

<!-- Bootstrap 5 Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

