<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Executive Dashboard" scope="request"/>
<c:set var="activePage" value="dashboard" scope="request"/>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- KPI Metric Cards Grid -->
<div class="row g-3 mb-4">
    <!-- 1. Total Products -->
    <div class="col-12 col-sm-6 col-xl-2">
        <div class="card-kpi">
            <div class="d-flex align-items-center justify-content-between mb-2">
                <span class="kpi-title">Total Products</span>
                <div class="kpi-icon-wrap icon-blue"><i class="bi bi-box"></i></div>
            </div>
            <div class="kpi-value"><c:out value="${totalProducts}"/></div>
            <small class="text-muted"><i class="bi bi-check2"></i> In catalog</small>
        </div>
    </div>

    <!-- 2. Total Stock Quantity -->
    <div class="col-12 col-sm-6 col-xl-2">
        <div class="card-kpi">
            <div class="d-flex align-items-center justify-content-between mb-2">
                <span class="kpi-title">Total Stock</span>
                <div class="kpi-icon-wrap icon-green"><i class="bi bi-layers"></i></div>
            </div>
            <div class="kpi-value"><c:out value="${totalStockQty}"/></div>
            <small class="text-muted"><i class="bi bi-boxes"></i> Physical units</small>
        </div>
    </div>

    <!-- 3. Low Stock Items -->
    <div class="col-12 col-sm-6 col-xl-2">
        <div class="card-kpi">
            <div class="d-flex align-items-center justify-content-between mb-2">
                <span class="kpi-title">Low Stock</span>
                <div class="kpi-icon-wrap icon-amber"><i class="bi bi-exclamation-circle"></i></div>
            </div>
            <div class="kpi-value text-warning"><c:out value="${lowStockCount}"/></div>
            <small class="text-muted"><i class="bi bi-arrow-down-short"></i> Need reorder</small>
        </div>
    </div>

    <!-- 4. Out of Stock Items -->
    <div class="col-12 col-sm-6 col-xl-2">
        <div class="card-kpi">
            <div class="d-flex align-items-center justify-content-between mb-2">
                <span class="kpi-title">Out of Stock</span>
                <div class="kpi-icon-wrap icon-red"><i class="bi bi-slash-circle"></i></div>
            </div>
            <div class="kpi-value text-danger"><c:out value="${outOfStockCount}"/></div>
            <small class="text-muted"><i class="bi bi-x-circle"></i> Depleted items</small>
        </div>
    </div>

    <!-- 5. Total Inventory Valuation -->
    <div class="col-12 col-sm-12 col-xl-4">
        <div class="card-kpi">
            <div class="d-flex align-items-center justify-content-between mb-2">
                <span class="kpi-title">Total Inventory Valuation</span>
                <div class="kpi-icon-wrap icon-purple"><i class="bi bi-currency-dollar"></i></div>
            </div>
            <div class="kpi-value text-primary">
                $<fmt:formatNumber value="${totalValuation}" pattern="#,##0.00"/>
            </div>
            <small class="text-muted"><i class="bi bi-cash-stack"></i> Asset valuation at purchase cost</small>
        </div>
    </div>
</div>

<!-- Middle Section: Chart and Low Stock Warnings -->
<div class="row g-4 mb-4">
    <!-- Stock Distribution Chart (Chart.js) -->
    <div class="col-12 col-lg-5">
        <div class="data-card h-100 mb-0">
            <div class="data-card-header">
                <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-pie-chart-fill text-primary me-2"></i>Stock by Category</h6>
                <span class="badge bg-light text-secondary border">Real-Time</span>
            </div>
            <div class="data-card-body d-flex flex-column align-items-center justify-content-center">
                <div style="height: 250px; width: 100%;">
                    <canvas id="categoryStockChart"
                            data-labels='<c:out value="${categoryLabelsJson}" escapeXml="false"/>'
                            data-values='<c:out value="${categoryDataJson}" escapeXml="false"/>'>
                    </canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Quick Stock Alerts -->
    <div class="col-12 col-lg-7">
        <div class="data-card h-100 mb-0">
            <div class="data-card-header">
                <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-bell-fill text-danger me-2"></i>Immediate Attention Required</h6>
                <a href="${pageContext.request.contextPath}/alerts" class="btn btn-sm btn-outline-danger">View All Alerts</a>
            </div>
            <div class="table-responsive">
                <table class="table table-custom">
                    <thead>
                        <tr>
                            <th>Product Name</th>
                            <th>SKU</th>
                            <th>Stock</th>
                            <th>Min Level</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty alertProducts}">
                                <tr>
                                    <td colspan="6" class="text-center text-success py-4">
                                        <i class="bi bi-shield-check fs-4 d-block mb-1"></i>
                                        All inventory items are currently above safety thresholds!
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="p" items="${alertProducts}" begin="0" end="4">
                                    <tr>
                                        <td class="fw-semibold text-dark"><c:out value="${p.productName}"/></td>
                                        <td><code><c:out value="${p.skuCode}"/></code></td>
                                        <td class="fw-bold <c:out value="${p.currentStock == 0 ? 'text-danger' : 'text-warning'}"/>">
                                            <c:out value="${p.currentStock}"/>
                                        </td>
                                        <td><c:out value="${p.minStockLevel}"/></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${p.currentStock == 0}">
                                                    <span class="badge-status badge-out-of-stock"><i class="bi bi-x-circle-fill"></i> Out of Stock</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-status badge-low-stock"><i class="bi bi-exclamation-triangle-fill"></i> Low Stock</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/stock" class="btn btn-sm btn-primary py-0 px-2">
                                                Restock
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
    </div>
</div>

<!-- Bottom Section: Recent Transactions Log -->
<div class="data-card">
    <div class="data-card-header">
        <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-clock-history text-primary me-2"></i>Recent Stock Movements</h6>
        <a href="${pageContext.request.contextPath}/stock" class="btn btn-sm btn-outline-primary">Complete Movement Log</a>
    </div>
    <div class="table-responsive">
        <table class="table table-custom">
            <thead>
                <tr>
                    <th>Date & Time</th>
                    <th>Type</th>
                    <th>Product</th>
                    <th>SKU</th>
                    <th>Quantity</th>
                    <th>Supplier / Reason</th>
                    <th>Ref #</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty recentTransactions}">
                        <tr>
                            <td colspan="7" class="text-center text-muted py-3">No recent transactions recorded.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="t" items="${recentTransactions}">
                            <tr>
                                <td class="text-muted"><fmt:formatDate value="${t.transactionDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${t.transactionType == 'IN'}">
                                            <span class="badge-status badge-stock-in"><i class="bi bi-arrow-down-left"></i> STOCK IN</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-status badge-stock-out"><i class="bi bi-arrow-up-right"></i> STOCK OUT</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="fw-semibold text-dark"><c:out value="${t.productName}"/></td>
                                <td><code><c:out value="${t.skuCode}"/></code></td>
                                <td class="fw-bold <c:out value="${t.transactionType == 'IN' ? 'text-success' : 'text-danger'}"/>">
                                    <c:out value="${t.transactionType == 'IN' ? '+' : '-'}"/><c:out value="${t.quantity}"/>
                                </td>
                                <td>
                                    <c:out value="${t.supplierName != null ? t.supplierName : t.reason}"/>
                                </td>
                                <td class="text-muted small"><c:out value="${t.referenceNumber}" default="-"/></td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

