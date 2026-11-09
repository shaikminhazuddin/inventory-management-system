<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Inventory Reports & Analytics" scope="request"/>
<c:set var="activePage" value="reports" scope="request"/>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- Report Configuration & Filters Bar -->
<div class="data-card mb-4">
    <div class="data-card-body">
        <form action="${pageContext.request.contextPath}/reports" method="GET" class="row g-3 align-items-end">
            <div class="col-12 col-md-3">
                <label class="form-label">Report Type</label>
                <select class="form-select" name="type" id="reportTypeSelect">
                    <option value="STOCK_SUMMARY" ${reportType == 'STOCK_SUMMARY' ? 'selected' : ''}>Product Stock Valuation</option>
                    <option value="STOCK_IN" ${reportType == 'STOCK_IN' ? 'selected' : ''}>Stock IN Movement Log</option>
                    <option value="STOCK_OUT" ${reportType == 'STOCK_OUT' ? 'selected' : ''}>Stock OUT Movement Log</option>
                </select>
            </div>

            <div class="col-12 col-md-3">
                <label class="form-label">Start Date</label>
                <input type="date" class="form-control" name="startDate" value="<c:out value='${startDate}'/>">
            </div>

            <div class="col-12 col-md-3">
                <label class="form-label">End Date</label>
                <input type="date" class="form-control" name="endDate" value="<c:out value='${endDate}'/>">
            </div>

            <div class="col-12 col-md-3 d-flex gap-2">
                <button type="submit" class="btn btn-primary flex-grow-1">
                    <i class="bi bi-filter me-1"></i> Apply Filter
                </button>
                <a href="${pageContext.request.contextPath}/reports?type=${reportType}&startDate=${startDate}&endDate=${endDate}&export=csv"
                   class="btn btn-success" title="Download Excel CSV">
                    <i class="bi bi-filetype-csv me-1"></i> Export CSV
                </a>
            </div>
        </form>
    </div>
</div>

<!-- Report Summary Header -->
<div class="row g-3 mb-4">
    <div class="col-md-6">
        <div class="card-kpi py-3">
            <span class="kpi-title">Total Active Inventory Units</span>
            <div class="kpi-value text-dark"><c:out value="${totalStockUnits}"/></div>
        </div>
    </div>
    <div class="col-md-6">
        <div class="card-kpi py-3">
            <span class="kpi-title">Total Inventory Portfolio Value</span>
            <div class="kpi-value text-primary">$<fmt:formatNumber value="${totalValuation}" pattern="#,##0.00"/></div>
        </div>
    </div>
</div>

<!-- Report Dynamic Table -->
<div class="data-card">
    <div class="data-card-header">
        <h6 class="mb-0 fw-bold text-dark">
            <i class="bi bi-table text-primary me-2"></i>
            <c:choose>
                <c:when test="${reportType == 'STOCK_IN'}">Stock IN Inbound Replenishment Ledger</c:when>
                <c:when test="${reportType == 'STOCK_OUT'}">Stock OUT Outbound Dispatch Ledger</c:when>
                <c:otherwise>Warehouse Stock Valuation & Asset Report</c:otherwise>
            </c:choose>
        </h6>
    </div>
    <div class="table-responsive">
        <c:choose>
            <%-- TRANSACTION REPORTS --%>
            <c:when test="${reportType == 'STOCK_IN' || reportType == 'STOCK_OUT'}">
                <table class="table table-custom">
                    <thead>
                        <tr>
                            <th>Transaction ID</th>
                            <th>Date & Time</th>
                            <th>Type</th>
                            <th>Product Name</th>
                            <th>SKU</th>
                            <th>Quantity</th>
                            <th>Partner / Supplier</th>
                            <th>Reference #</th>
                            <th>Reason / Remarks</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty transactions}">
                                <tr>
                                    <td colspan="9" class="text-center text-muted py-4">No records found for the selected period.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="t" items="${transactions}">
                                    <c:if test="${(reportType == 'STOCK_IN' && t.transactionType == 'IN') || (reportType == 'STOCK_OUT' && t.transactionType == 'OUT')}">
                                        <tr>
                                            <td class="text-muted">#<c:out value="${t.transactionId}"/></td>
                                            <td><fmt:formatDate value="${t.transactionDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                                            <td>
                                                <span class="badge-status ${t.transactionType == 'IN' ? 'badge-stock-in' : 'badge-stock-out'}">
                                                    <c:out value="${t.transactionType}"/>
                                                </span>
                                            </td>
                                            <td class="fw-semibold text-dark"><c:out value="${t.productName}"/></td>
                                            <td><code><c:out value="${t.skuCode}"/></code></td>
                                            <td class="fw-bold <c:out value="${t.transactionType == 'IN' ? 'text-success' : 'text-danger'}"/>">
                                                <c:out value="${t.quantity}"/>
                                            </td>
                                            <td><c:out value="${t.supplierName}" default="-"/></td>
                                            <td><c:out value="${t.referenceNumber}" default="-"/></td>
                                            <td class="text-muted small"><c:out value="${t.reason}" default="-"/></td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </c:when>

            <%-- PRODUCT STOCK VALUATION REPORT --%>
            <c:otherwise>
                <table class="table table-custom">
                    <thead>
                        <tr>
                            <th>Product Name</th>
                            <th>SKU Code</th>
                            <th>Category</th>
                            <th>Cost Price</th>
                            <th>Selling Price</th>
                            <th>Current Stock</th>
                            <th>Status</th>
                            <th class="text-end">Inventory Value ($)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${products}">
                            <tr>
                                <td class="fw-semibold text-dark"><c:out value="${p.productName}"/></td>
                                <td><code><c:out value="${p.skuCode}"/></code></td>
                                <td><span class="badge bg-light text-dark border"><c:out value="${p.categoryName}"/></span></td>
                                <td>$<fmt:formatNumber value="${p.purchasePrice}" pattern="#,##0.00"/></td>
                                <td>$<fmt:formatNumber value="${p.sellingPrice}" pattern="#,##0.00"/></td>
                                <td class="fw-bold"><c:out value="${p.currentStock}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.alertStatus == 'OUT_OF_STOCK'}">
                                            <span class="badge-status badge-out-of-stock">OUT OF STOCK</span>
                                        </c:when>
                                        <c:when test="${p.alertStatus == 'LOW_STOCK'}">
                                            <span class="badge-status badge-low-stock">LOW STOCK</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-status badge-normal">NORMAL</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end fw-bold text-dark">
                                    $<fmt:formatNumber value="${p.inventoryValue}" pattern="#,##0.00"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

