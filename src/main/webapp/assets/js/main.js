/**
 * Main Application Frontend Utilities
 */
document.addEventListener("DOMContentLoaded", function () {
    // 1. Mobile Sidebar Toggle
    const sidebarToggle = document.getElementById("sidebarToggle");
    const sidebar = document.querySelector(".app-sidebar");
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener("click", function () {
            sidebar.classList.toggle("show");
        });
    }

    // 2. Auto-dismiss alerts after 5 seconds
    const flashAlerts = document.querySelectorAll(".alert-dismissible");
    flashAlerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        }, 5000);
    });

    // 3. Delete Confirmation Interceptor
    const deleteButtons = document.querySelectorAll(".btn-confirm-delete");
    deleteButtons.forEach(function (btn) {
        btn.addEventListener("click", function (e) {
            const itemName = btn.getAttribute("data-name") || "this item";
            if (!confirm("Are you sure you want to permanently delete " + itemName + "?")) {
                e.preventDefault();
            }
        });
    });
});

