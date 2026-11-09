<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Stock Operations" scope="request"/>
<c:set var="activePage" value="stock" scope="request"/>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- Stock In & Stock Out Action Cards -->
<div class="row g-4 mb-4">
    <!-- STOCK IN FORM -->
    <div class="col-12 col-lg-6">
        <div class="data-card border-top border-4 border-primary h-100">
            <div class="data-card-header bg-light">
                <h6 class="mb-0 fw-bold text-primary">
                    <i class="bi bi-box-arrow-in-down me-2"></i>Stock IN (Inbound Receiving)
                </h6>
            </div>
            <div class="data-card-body">
                <form action="${pageContext.request.contextPath}/stock" method="POST">
                    <input type="hidden" name="action" value="in">

                    <div class="mb-3">
                        <label class="form-label">Select Product <span class="text-danger">*</span></label>
                        <select class="form-select" name="productId" required>
                            <option value="">-- Choose Item to Receive --</option>
                            <c:forEach var="p" items="${products}">
                                <option value="${p.productId}">
                                    <c:out value="${p.productName}"/> (Current Stock: ${p.currentStock}) [${p.skuCode}]
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="row g-2 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Quantity <span class="text-danger">*</span></label>
                            <input type="number" min="1" class="form-control" name="quantity" required placeholder="Units received">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Supplier (Vendor)</label>
                            <select class="form-select" name="supplierId">
                                <option value="">-- Select Supplier --</option>
                                <c:forEach var="s" items="${suppliers}">
                                    <option value="${s.supplierId}"><c:out value="${s.supplierName}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="row g-2 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">PO / Delivery Reference #</label>
                            <input type="text" class="form-control" name="referenceNumber" placeholder="e.g. PO-2026-0099">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Reason / Notes</label>
                            <input type="text" class="form-control" name="reason" placeholder="e.g. Monthly purchase restocking">
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100">
                        <i class="bi bi-check2-circle me-1"></i> Confirm Stock IN
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- STOCK OUT FORM -->
    <div class="col-12 col-lg-6">
        <div class="data-card border-top border-4 border-danger h-100">
            <div class="data-card-header bg-light">
                <h6 class="mb-0 fw-bold text-danger">
                    <i class="bi bi-box-arrow-up-right me-2"></i>Stock OUT (Outbound Dispatch)
                </h6>
            </div>
            <div class="data-card-body">
                <form action="${pageContext.request.contextPath}/stock" method="POST">
                    <input type="hidden" name="action" value="out">

                    <div class="mb-3">
                        <label class="form-label">Select Product <span class="text-danger">*</span></label>
                        <select class="form-select" name="productId" required id="stockOutProductSelect">
                            <option value="">-- Choose Item to Deduct --</option>
                            <c:forEach var="p" items="${products}">
                                <option value="${p.productId}" data-stock="${p.currentStock}">
                                    <c:out value="${p.productName}"/> (Available: ${p.currentStock}) [${p.skuCode}]
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="row g-2 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Quantity <span class="text-danger">*</span></label>
                            <input type="number" min="1" class="form-control" name="quantity" required placeholder="Units to dispatch">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Sales Order / Dispatch Ref #</label>
                            <input type="text" class="form-control" name="referenceNumber" placeholder="e.g. SO-10934">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Reason for Stock Removal <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="reason" required placeholder="e.g. Customer delivery, damage write-off, production transfer">
                    </div>

                    <button type="submit" class="btn btn-danger w-100">
                        <i class="bi bi-dash-circle me-1"></i> Confirm Stock OUT
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Stock Transaction Audit Trail Table -->
<div class="data-card">
    <div class="data-card-header">
        <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-clock-history text-primary me-2"></i>Movement Audit Trail (Last 50 Events)</h6>
        <span class="badge bg-light text-secondary border">Real-Time Sync</span>
    </div>
    <div class="table-responsive">
        <table class="table table-custom">
            <thead>
                <tr>
                    <th>Date & Time</th>
                    <th>Type</th>
                    <th>Product Name</th>
                    <th>SKU Code</th>
                    <th>Quantity</th>
                    <th>Supplier / Origin</th>
                    <th>Reference #</th>
                    <th>Reason / Notes</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty transactions}">
                        <tr>
                            <td colspan="8" class="text-center text-muted py-4">No stock movements recorded yet.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="t" items="${transactions}">
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
                                <td><c:out value="${t.supplierName}" default="-"/></td>
                                <td><c:out value="${t.referenceNumber}" default="-"/></td>
                                <td class="text-muted small"><c:out value="${t.reason}" default="-"/></td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

