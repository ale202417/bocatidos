const currencyChartColor = "#bd5d38";
const gridColor = "rgba(73, 37, 29, 0.08)";

const salesCanvas = document.getElementById("salesTrendChart");
if (salesCanvas && window.dashboardData) {
    new Chart(salesCanvas, {
        type: "line",
        data: {
            labels: window.dashboardData.salesLabels,
            datasets: [{
                data: window.dashboardData.salesValues,
                label: "Revenue",
                borderColor: currencyChartColor,
                backgroundColor: "rgba(189, 93, 56, 0.14)",
                fill: true,
                tension: 0.35,
                pointRadius: 4,
                pointHoverRadius: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {display: false}
            },
            scales: {
                x: {
                    grid: {display: false}
                },
                y: {
                    beginAtZero: true,
                    grid: {color: gridColor}
                }
            }
        }
    });
}

const expenseCanvas = document.getElementById("expenseChart");
if (expenseCanvas && window.dashboardData) {
    new Chart(expenseCanvas, {
        type: "doughnut",
        data: {
            labels: window.dashboardData.expenseLabels,
            datasets: [{
                data: window.dashboardData.expenseValues,
                backgroundColor: ["#bd5d38", "#f0a46a", "#7f9c74", "#4d6a89"],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: "bottom"
                }
            },
            cutout: "66%"
        }
    });
}
