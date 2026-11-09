<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Supplier Directory" scope="request"/>
<c:set var="activePage" value="suppliers" scope="request"/>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <p class="text-muted mb-0">Maintain vendor profiles, contact details, and procurement partners.</p>
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addSupplierModal">
        <i class="bi bi-plus-lg me-1"></i> Register Supplier
    </button>
</div>

<!-- Suppliers Table -->
<div class="data-card">
    <div class="data-card-header">
        <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-truck text-primary me-2"></i>Approved Vendors (${suppliers.size()})</h6>
    </div>
    <div class="table-responsive">
        <table class="table table-custom">
            <thead>
                <tr>
                    <th style="width: 70px;">ID</th>
                    <th>Supplier / Company</th>
                    <th>Contact Person</th>
                    <th>Phone</th>
                    <th>Email</th>
                    <th>Address</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty suppliers}">
                        <tr>
                            <td colspan="7" class="text-center text-muted py-4">No suppliers registered yet.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="s" items="${suppliers}">
                            <tr>
                                <td class="text-muted">#<c:out value="${s.supplierId}"/></td>
                                <td class="fw-bold text-dark"><c:out value="${s.supplierName}"/></td>
                                <td><c:out value="${s.contactPerson}" default="-"/></td>
                                <td><c:out value="${s.phone}" default="-"/></td>
                                <td>
                                    <c:if test="${not empty s.email}">
                                        <a href="mailto:${s.email}" class="text-decoration-none"><c:out value="${s.email}"/></a>
                                    </c:if>
                                    <c:if test="${empty s.email}">-</c:if>
                                </td>
                                <td class="text-muted small"><c:out value="${s.address}" default="-"/></td>
                                <td class="text-end">
                                    <button type="button" class="btn btn-sm btn-outline-primary me-1"
                                            onclick="openEditSupplierModal('${s.supplierId}', '${s.supplierName}', '${s.contactPerson}', '${s.phone}', '${s.email}', '${s.address}')">
                                        <i class="bi bi-pencil"></i>
                                    </button>
                                    <a href="${pageContext.request.contextPath}/suppliers?action=delete&id=${s.supplierId}"
                                       class="btn btn-sm btn-outline-danger btn-confirm-delete"
                                       data-name="<c:out value='${s.supplierName}'/>">
                                        <i class="bi bi-trash"></i>
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

<!-- ========================================================================= -->
<!-- ADD SUPPLIER MODAL                                                        -->
<!-- ========================================================================= -->
<div class="modal fade" id="addSupplierModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/suppliers" method="POST">
                <input type="hidden" name="action" value="add">
                
                <div class="modal-header">
                    <h5 class="modal-title fw-bold"><i class="bi bi-person-plus-fill text-primary me-2"></i>Register New Supplier</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Company / Supplier Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="supplierName" required placeholder="e.g. Apex Global Components">
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Contact Person</label>
                            <input type="text" class="form-control" name="contactPerson" placeholder="e.g. John Doe">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Phone Number</label>
                            <input type="text" class="form-control" name="phone" placeholder="e.g. +1-555-0192">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Email Address</label>
                        <input type="email" class="form-control" name="email" placeholder="e.g. orders@supplier.com">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Physical / Warehouse Address</label>
                        <textarea class="form-control" name="address" rows="2" placeholder="Street, City, State, ZIP..."></textarea>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i> Save Supplier</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- ========================================================================= -->
<!-- EDIT SUPPLIER MODAL                                                       -->
<!-- ========================================================================= -->
<div class="modal fade" id="editSupplierModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/suppliers" method="POST">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" id="editSupplierId" name="supplierId">
                
                <div class="modal-header">
                    <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square text-primary me-2"></i>Edit Supplier</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Company Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="editSupplierName" name="supplierName" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Contact Person</label>
                            <input type="text" class="form-control" id="editContactPerson" name="contactPerson">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Phone</label>
                            <input type="text" class="form-control" id="editPhone" name="phone">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Email</label>
                        <input type="email" class="form-control" id="editEmail" name="email">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Address</label>
                        <textarea class="form-control" id="editAddress" name="address" rows="2"></textarea>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-arrow-repeat me-1"></i> Update Supplier</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function openEditSupplierModal(id, name, contact, phone, email, address) {
    document.getElementById('editSupplierId').value = id;
    document.getElementById('editSupplierName').value = name;
    document.getElementById('editContactPerson').value = contact;
    document.getElementById('editPhone').value = phone;
    document.getElementById('editEmail').value = email;
    document.getElementById('editAddress').value = address;

    new bootstrap.Modal(document.getElementById('editSupplierModal')).show();
}
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

