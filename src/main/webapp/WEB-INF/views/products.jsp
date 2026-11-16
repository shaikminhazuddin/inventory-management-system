<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Product Management" scope="request"/>
<c:set var="activePage" value="products" scope="request"/>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- Top Toolbar: Search, Filter, and Add Button -->
<div class="data-card mb-4">
    <div class="data-card-body">
        <form action="${pageContext.request.contextPath}/products" method="GET" class="row g-3 align-items-center">
            <div class="col-12 col-md-5">
                <div class="input-group">
                    <span class="input-group-text bg-white text-secondary border-end-0"><i class="bi bi-search"></i></span>
                    <input type="text" class="form-control border-start-0" name="keyword"
                           placeholder="Search by product name, SKU, or barcode..."
                           value="<c:out value='${searchKeyword}'/>">
                </div>
            </div>

            <div class="col-12 col-md-4">
                <select class="form-select" name="categoryId" onchange="this.form.submit()">
                    <option value="">All Categories</option>
                    <c:forEach var="c" items="${categories}">
                        <option value="${c.categoryId}" ${selectedCategory == c.categoryId ? 'selected' : ''}>
                            <c:out value="${c.categoryName}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-12 col-md-3 d-flex gap-2 justify-content-md-end">
                <button type="submit" class="btn btn-outline-secondary">Filter</button>
                <c:if test="${not empty searchKeyword || not empty selectedCategory}">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-link text-decoration-none text-muted">Clear</a>
                </c:if>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addProductModal">
                    <i class="bi bi-plus-lg me-1"></i> Add Product
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Products Table -->
<div class="data-card">
    <div class="data-card-header">
        <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-boxes text-primary me-2"></i>Product Catalog (${products.size()} items)</h6>
    </div>
    <div class="table-responsive">
        <table class="table table-custom">
            <thead>
                <tr>
                    <th>Product Name</th>
                    <th>SKU Code</th>
                    <th>Barcode</th>
                    <th>Category</th>
                    <th>Buy Price</th>
                    <th>Sell Price</th>
                    <th>Current Stock</th>
                    <th>Min Level</th>
                    <th>Status</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty products}">
                        <tr>
                            <td colspan="10" class="text-center text-muted py-4">
                                <i class="bi bi-inbox fs-3 d-block mb-2"></i>
                                No products found matching your search criteria.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="p" items="${products}">
                            <tr>
                                <td class="fw-semibold text-dark">
                                    <c:out value="${p.productName}"/>
                                    <c:if test="${not empty p.description}">
                                        <small class="text-muted d-block" style="font-size: 0.78rem;">
                                            <c:out value="${p.description}"/>
                                        </small>
                                    </c:if>
                                </td>
                                <td><code><c:out value="${p.skuCode}"/></code></td>
                                <td class="text-muted small"><c:out value="${p.barcode}" default="-"/></td>
                                <td><span class="badge bg-light text-dark border"><c:out value="${p.categoryName}"/></span></td>
                                <td>$<fmt:formatNumber value="${p.purchasePrice}" pattern="#,##0.00"/></td>
                                <td>$<fmt:formatNumber value="${p.sellingPrice}" pattern="#,##0.00"/></td>
                                <td class="fw-bold">
                                    <c:out value="${p.currentStock}"/>
                                </td>
                                <td class="text-muted"><c:out value="${p.minStockLevel}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.alertStatus == 'OUT_OF_STOCK'}">
                                            <span class="badge-status badge-out-of-stock"><i class="bi bi-x-circle-fill"></i> Out of Stock</span>
                                        </c:when>
                                        <c:when test="${p.alertStatus == 'LOW_STOCK'}">
                                            <span class="badge-status badge-low-stock"><i class="bi bi-exclamation-triangle-fill"></i> Low Stock</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-status badge-normal"><i class="bi bi-check-circle-fill"></i> Normal</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <button type="button" class="btn btn-sm btn-outline-primary me-1 btn-edit-product"
                                            data-id="${p.productId}"
                                            data-name="<c:out value='${p.productName}'/>"
                                            data-sku="<c:out value='${p.skuCode}'/>"
                                            data-barcode="<c:out value='${p.barcode}'/>"
                                            data-category="${p.categoryId}"
                                            data-buy="${p.purchasePrice}"
                                            data-sell="${p.sellingPrice}"
                                            data-min="${p.minStockLevel}"
                                            data-desc="<c:out value='${p.description}'/>"
                                            title="Edit Product">
                                        <i class="bi bi-pencil"></i>
                                    </button>
                                    <a href="${pageContext.request.contextPath}/products?action=delete&id=${p.productId}"
                                       class="btn btn-sm btn-outline-danger btn-confirm-delete"
                                       data-name="<c:out value='${p.productName}'/>"
                                       title="Delete Product">
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
<!-- ADD PRODUCT MODAL                                                         -->
<!-- ========================================================================= -->
<div class="modal fade" id="addProductModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/products" method="POST">
                <input type="hidden" name="action" value="add">
                
                <div class="modal-header">
                    <h5 class="modal-title fw-bold"><i class="bi bi-plus-circle-fill text-primary me-2"></i>Add New Product</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Product Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="productName" required placeholder="e.g. Wireless Barcode Scanner">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">SKU Code (Unique) <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="skuCode" required placeholder="e.g. SKU-SCN-001">
                        </div>
                        
                        <div class="col-md-6">
                            <label class="form-label">Category <span class="text-danger">*</span></label>
                            <select class="form-select" name="categoryId" required>
                                <option value="">Select Category</option>
                                <c:forEach var="c" items="${categories}">
                                    <option value="${c.categoryId}"><c:out value="${c.categoryName}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Barcode (EAN/UPC)</label>
                            <input type="text" class="form-control" name="barcode" placeholder="e.g. 890123456789">
                        </div>

                        <div class="col-md-3">
                            <label class="form-label">Purchase Price ($) <span class="text-danger">*</span></label>
                            <input type="number" step="0.01" min="0" class="form-control" name="purchasePrice" required value="0.00">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Selling Price ($) <span class="text-danger">*</span></label>
                            <input type="number" step="0.01" min="0" class="form-control" name="sellingPrice" required value="0.00">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Initial Stock <span class="text-danger">*</span></label>
                            <input type="number" min="0" class="form-control" name="currentStock" required value="0">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Min Stock Level <span class="text-danger">*</span></label>
                            <input type="number" min="1" class="form-control" name="minStockLevel" required value="10">
                        </div>

                        <div class="col-12">
                            <label class="form-label">Description</label>
                            <textarea class="form-control" name="description" rows="2" placeholder="Specifications, warranty, package details..."></textarea>
                        </div>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i> Save Product</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- ========================================================================= -->
<!-- EDIT PRODUCT MODAL                                                        -->
<!-- ========================================================================= -->
<div class="modal fade" id="editProductModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/products" method="POST">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" id="editProductId" name="productId">
                
                <div class="modal-header">
                    <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square text-primary me-2"></i>Edit Product</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Product Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="editProductName" name="productName" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">SKU Code (Unique) <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="editSkuCode" name="skuCode" required>
                        </div>
                        
                        <div class="col-md-6">
                            <label class="form-label">Category <span class="text-danger">*</span></label>
                            <select class="form-select" id="editCategoryId" name="categoryId" required>
                                <c:forEach var="c" items="${categories}">
                                    <option value="${c.categoryId}"><c:out value="${c.categoryName}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Barcode</label>
                            <input type="text" class="form-control" id="editBarcode" name="barcode">
                        </div>

                        <div class="col-md-4">
                            <label class="form-label">Purchase Price ($) <span class="text-danger">*</span></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="editPurchasePrice" name="purchasePrice" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Selling Price ($) <span class="text-danger">*</span></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="editSellingPrice" name="sellingPrice" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Min Stock Level <span class="text-danger">*</span></label>
                            <input type="number" min="1" class="form-control" id="editMinStockLevel" name="minStockLevel" required>
                        </div>

                        <div class="col-12">
                            <label class="form-label">Description</label>
                            <textarea class="form-control" id="editDescription" name="description" rows="2"></textarea>
                        </div>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-arrow-repeat me-1"></i> Update Product</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll('.btn-edit-product').forEach(function(btn) {
        btn.addEventListener('click', function() {
            document.getElementById('editProductId').value = this.dataset.id || '';
            document.getElementById('editProductName').value = this.dataset.name || '';
            document.getElementById('editSkuCode').value = this.dataset.sku || '';
            document.getElementById('editBarcode').value = this.dataset.barcode || '';
            document.getElementById('editCategoryId').value = this.dataset.category || '';
            document.getElementById('editPurchasePrice').value = this.dataset.buy || '';
            document.getElementById('editSellingPrice').value = this.dataset.sell || '';
            document.getElementById('editMinStockLevel').value = this.dataset.min || '';
            document.getElementById('editDescription').value = this.dataset.desc || '';
            new bootstrap.Modal(document.getElementById('editProductModal')).show();
        });
    });
});
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

