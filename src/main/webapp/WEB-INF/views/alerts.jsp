<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Low Stock & Replenishment Alerts" scope="request"/>
<c:set var="activePage" value="alerts" scope="request"/>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- Alert KPI Cards -->
<div class="row g-3 mb-4">
    <div class="col-12 col-md-6">
        <div class="card-kpi border-start border-4 border-danger">
            <div class="d-flex align-items-center justify-content-between mb-2">
                <span class="kpi-title text-danger">Out of Stock (Zero Inventory)</span>
                <div class="kpi-icon-wrap icon-red"><i class="bi bi-x-octagon-fill"></i></div>
            </div>
            <div class="kpi-value text-danger"><c:out value="${outOfStockCount}"/></div>
            <small class="text-muted">Immediate supply replenishment required to prevent stockout losses.</small>
        </div>
    </div>

    <div class="col-12 col-md-6">
        <div class="card-kpi border-start border-4 border-warning">
            <div class="d-flex align-items-center justify-content-between mb-2">
                <span class="kpi-title text-warning">Low Stock (At or Below Threshold)</span>
                <div class="kpi-icon-wrap icon-amber"><i class="bi bi-exclamation-triangle-fill"></i></div>
            </div>
            <div class="kpi-value text-warning"><c:out value="${lowStockCount}"/></div>
            <small class="text-muted">Items running below minimum safety threshold.</small>
        </div>
    </div>
</div>

<!-- Alerts Table -->
<div class="data-card">
    <div class="data-card-header">
        <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-bell-fill text-danger me-2"></i>Active Inventory Alert Watchlist</h6>
        <a href="${pageContext.request.contextPath}/stock" class="btn btn-sm btn-primary">
            <i class="bi bi-plus-circle me-1"></i> Quick Restock IN
        </a>
    </div>
    <div class="table-responsive">
        <table class="table table-custom">
            <thead>
                <tr>
                    <th>Product Name</th>
                    <th>SKU Code</th>
                    <th>Category</th>
                    <th>Current Stock</th>
                    <th>Min Safety Level</th>
                    <th>Replenishment Deficit</th>
                    <th>Alert Severity</th>
                    <th class="text-end">Quick Action</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty alertProducts}">
                        <tr>
                            <td colspan="8" class="text-center text-success py-5">
                                <i class="bi bi-check-circle-fill fs-2 d-block mb-2"></i>
                                <span class="fw-semibold">All inventory levels are safe and above minimum thresholds!</span>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="p" items="${alertProducts}">
                            <tr>
                                <td class="fw-bold text-dark"><c:out value="${p.productName}"/></td>
                                <td><code><c:out value="${p.skuCode}"/></code></td>
                                <td><span class="badge bg-light text-dark border"><c:out value="${p.categoryName}"/></span></td>
                                <td class="fw-bold <c:out value="${p.currentStock == 0 ? 'text-danger' : 'text-warning'}"/>">
                                    <c:out value="${p.currentStock}"/> units
                                </td>
                                <td><c:out value="${p.minStockLevel}"/> units</td>
                                <td class="text-danger fw-semibold">
                                    <c:choose>
                                        <c:when test="${p.minStockLevel > p.currentStock}">
                                            -<c:out value="${p.minStockLevel - p.currentStock}"/> units needed
                                        </c:when>
                                        <c:otherwise>At minimum</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.currentStock == 0}">
                                            <span class="badge-status badge-out-of-stock">
                                                <i class="bi bi-slash-circle-fill"></i> CRITICAL: OUT OF STOCK
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-status badge-low-stock">
                                                <i class="bi bi-exclamation-triangle-fill"></i> WARNING: LOW STOCK
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <a href="${pageContext.request.contextPath}/stock" class="btn btn-sm btn-primary">
                                        <i class="bi bi-box-arrow-in-down me-1"></i> Stock IN
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

