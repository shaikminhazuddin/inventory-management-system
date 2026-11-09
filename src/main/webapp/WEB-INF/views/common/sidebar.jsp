<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside class="app-sidebar">
    <div class="sidebar-header d-flex align-items-center">
        <span class="brand-badge"><i class="bi bi-box-seam"></i></span>
        <div>
            <div class="fw-bold text-white fs-6 lh-1">StockMaster</div>
            <small class="text-secondary" style="font-size: 0.75rem;">Warehouse Inventory</small>
        </div>
    </div>

    <div class="sidebar-nav">
        <div class="sidebar-heading">Main Operations</div>

        <a href="${pageContext.request.contextPath}/dashboard"
           class="nav-link-custom ${activePage == 'dashboard' ? 'active' : ''}">
            <i class="bi bi-grid-1x2-fill"></i>
            <span>Dashboard</span>
        </a>

        <a href="${pageContext.request.contextPath}/products"
           class="nav-link-custom ${activePage == 'products' ? 'active' : ''}">
            <i class="bi bi-boxes"></i>
            <span>Products</span>
        </a>

        <a href="${pageContext.request.contextPath}/categories"
           class="nav-link-custom ${activePage == 'categories' ? 'active' : ''}">
            <i class="bi bi-tags-fill"></i>
            <span>Categories</span>
        </a>

        <a href="${pageContext.request.contextPath}/stock"
           class="nav-link-custom ${activePage == 'stock' ? 'active' : ''}">
            <i class="bi bi-arrow-left-right"></i>
            <span>Stock Operations</span>
        </a>

        <a href="${pageContext.request.contextPath}/alerts"
           class="nav-link-custom ${activePage == 'alerts' ? 'active' : ''}">
            <i class="bi bi-exclamation-triangle-fill"></i>
            <span>Low Stock Alerts</span>
        </a>

        <div class="sidebar-heading mt-3">Partners & Reports</div>

        <a href="${pageContext.request.contextPath}/suppliers"
           class="nav-link-custom ${activePage == 'suppliers' ? 'active' : ''}">
            <i class="bi bi-truck"></i>
            <span>Suppliers</span>
        </a>

        <a href="${pageContext.request.contextPath}/reports"
           class="nav-link-custom ${activePage == 'reports' ? 'active' : ''}">
            <i class="bi bi-file-earmark-bar-graph-fill"></i>
            <span>Reports & Exports</span>
        </a>
    </div>

    <div class="sidebar-footer">
        <div class="d-flex align-items-center justify-content-between">
            <div class="d-flex align-items-center overflow-hidden">
                <i class="bi bi-person-circle fs-4 text-white me-2"></i>
                <div class="text-truncate">
                    <div class="text-white fw-semibold small text-truncate">
                        <c:out value="${sessionScope.admin.fullName}" default="Administrator"/>
                    </div>
                    <small class="text-secondary" style="font-size: 0.7rem;">Warehouse Admin</small>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-danger border-0 p-1" title="Sign Out">
                <i class="bi bi-box-arrow-right fs-5"></i>
            </a>
        </div>
    </div>
</aside>

