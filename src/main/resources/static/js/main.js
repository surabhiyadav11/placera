/**
 * PLACERA - Client-Side Interactivity
 * Handles mobile sidebar drawer, live table filtering, confirmation dialogs, and alert dismissal.
 */
document.addEventListener('DOMContentLoaded', () => {
    // 1. Mobile Sidebar Toggle
    const toggleBtn = document.getElementById('sidebarToggleBtn');
    const sidebar = document.querySelector('.portal-sidebar');

    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });

        // Close sidebar if user clicks outside on mobile
        document.addEventListener('click', (e) => {
            if (sidebar.classList.contains('open') &&
                !sidebar.contains(e.target) &&
                !toggleBtn.contains(e.target)) {
                sidebar.classList.remove('open');
            }
        });
    }

    // 2. Real-time Client Table Filter
    const searchInputs = document.querySelectorAll('[data-table-search]');
    searchInputs.forEach((input) => {
        const tableId = input.getAttribute('data-table-search');
        const table = document.getElementById(tableId);

        if (table) {
            input.addEventListener('input', (e) => {
                const query = e.target.value.toLowerCase().trim();
                const rows = table.querySelectorAll('tbody tr');

                rows.forEach((row) => {
                    const text = row.textContent.toLowerCase();
                    if (text.includes(query)) {
                        row.style.display = '';
                    } else {
                        row.style.display = 'none';
                    }
                });
            });
        }
    });

    // 3. Auto-Dismiss Alert Toasts
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach((alert) => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.4s ease';
            setTimeout(() => alert.remove(), 400);
        }, 5000);
    });
});

/**
 * Global helper to prompt for confirmation on critical actions
 */
function confirmAction(message) {
    return confirm(message || 'Are you sure you want to perform this action?');
}
