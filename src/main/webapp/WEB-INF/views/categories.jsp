<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Category Management" scope="request"/>
<c:set var="activePage" value="categories" scope="request"/>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <p class="text-muted mb-0">Organize and group warehouse inventory by logical classifications.</p>
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCategoryModal">
        <i class="bi bi-plus-lg me-1"></i> Add Category
    </button>
</div>

<!-- Categories Table -->
<div class="data-card">
    <div class="data-card-header">
        <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-tags-fill text-primary me-2"></i>Product Categories (${categories.size()})</h6>
    </div>
    <div class="table-responsive">
        <table class="table table-custom">
            <thead>
                <tr>
                    <th style="width: 80px;">ID</th>
                    <th>Category Name</th>
                    <th>Description</th>
                    <th>Product Count</th>
                    <th>Created On</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty categories}">
                        <tr>
                            <td colspan="6" class="text-center text-muted py-4">No categories created yet.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="c" items="${categories}">
                            <tr>
                                <td class="text-muted">#<c:out value="${c.categoryId}"/></td>
                                <td class="fw-bold text-dark"><c:out value="${c.categoryName}"/></td>
                                <td class="text-muted"><c:out value="${c.description}" default="-"/></td>
                                <td>
                                    <span class="badge bg-primary-subtle text-primary border border-primary-subtle px-2 py-1">
                                        <c:out value="${c.productCount}"/> products
                                    </span>
                                </td>
                                <td class="text-muted small"><fmt:formatDate value="${c.createdAt}" pattern="yyyy-MM-dd"/></td>
                                <td class="text-end">
                                    <button type="button" class="btn btn-sm btn-outline-primary me-1 btn-edit-category"
                                            data-id="${c.categoryId}"
                                            data-name="<c:out value='${c.categoryName}'/>"
                                            data-desc="<c:out value='${c.description}'/>"
                                            title="Edit Category">
                                        <i class="bi bi-pencil"></i>
                                    </button>
                                    <a href="${pageContext.request.contextPath}/categories?action=delete&id=${c.categoryId}"
                                       class="btn btn-sm btn-outline-danger btn-confirm-delete"
                                       data-name="<c:out value='${c.categoryName}'/>"
                                       title="Delete Category">
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
<!-- ADD CATEGORY MODAL                                                        -->
<!-- ========================================================================= -->
<div class="modal fade" id="addCategoryModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/categories" method="POST">
                <input type="hidden" name="action" value="add">
                
                <div class="modal-header">
                    <h5 class="modal-title fw-bold"><i class="bi bi-tag-fill text-primary me-2"></i>Add New Category</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Category Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="categoryName" required placeholder="e.g. Electrical Tools">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea class="form-control" name="description" rows="3" placeholder="Category purpose or scope..."></textarea>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i> Save Category</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- ========================================================================= -->
<!-- EDIT CATEGORY MODAL                                                       -->
<!-- ========================================================================= -->
<div class="modal fade" id="editCategoryModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/categories" method="POST">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" id="editCategoryId" name="categoryId">
                
                <div class="modal-header">
                    <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square text-primary me-2"></i>Edit Category</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Category Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="editCategoryName" name="categoryName" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea class="form-control" id="editCategoryDescription" name="description" rows="3"></textarea>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-arrow-repeat me-1"></i> Update Category</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll('.btn-edit-category').forEach(function(btn) {
        btn.addEventListener('click', function() {
            document.getElementById('editCategoryId').value = this.dataset.id || '';
            document.getElementById('editCategoryName').value = this.dataset.name || '';
            document.getElementById('editCategoryDescription').value = this.dataset.desc || '';
            new bootstrap.Modal(document.getElementById('editCategoryModal')).show();
        });
    });
});
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

