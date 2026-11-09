/**
 * Dashboard Chart.js Integration
 * Reads JSON dataset dynamically injected by DashboardServlet from MySQL.
 */
document.addEventListener("DOMContentLoaded", function () {
    const chartCanvas = document.getElementById("categoryStockChart");
    if (!chartCanvas) return;

    try {
        const labelsData = JSON.parse(chartCanvas.getAttribute("data-labels") || "[]");
        const valuesData = JSON.parse(chartCanvas.getAttribute("data-values") || "[]");

        if (labelsData.length === 0) {
            return;
        }

        const colors = [
            "#2563eb", // Royal Blue
            "#10b981", // Emerald Green
            "#f59e0b", // Amber
            "#8b5cf6", // Purple
            "#06b6d4", // Cyan
            "#ec4899", // Pink
            "#64748b"  // Slate
        ];

        new Chart(chartCanvas, {
            type: "doughnut",
            data: {
                labels: labelsData,
                datasets: [{
                    data: valuesData,
                    backgroundColor: colors.slice(0, labelsData.length),
                    borderWidth: 2,
                    borderColor: "#ffffff",
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: "bottom",
                        labels: {
                            boxWidth: 12,
                            padding: 15,
                            font: {
                                size: 12,
                                family: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
                            }
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                const label = context.label || "";
                                const val = context.raw || 0;
                                return " " + label + ": " + val + " units in stock";
                            }
                        }
                    }
                },
                cutout: "68%"
            }
        });
    } catch (e) {
        console.error("Error rendering Category Stock Chart:", e);
    }
});

