
let data = null;

document.addEventListener("DOMContentLoaded", function() {
    fetchReport();  
});

const urlParams = new URLSearchParams(window.location.search);

// Fetch the startDate and endDate from the URL
const startDate = urlParams.get('startDate');
const endDate = urlParams.get('endDate');

async function fetchReport() {
    if (!startDate || !endDate) {
        alert("Please select both start and end dates.");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/reports/wellness?startDateTime=${startDate}&endDateTime=${endDate}`,{
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem("jwt")}` 
            }
    });
        
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
         data = await response.json();
        displayReport(data);
    } catch (error) {
        console.error("Error fetching report:", error);
        document.getElementById("reportContainer").innerHTML = "<p>Error loading report. Check console.</p>";
    }
}


function displayReport(data) {
    const reportContainer = document.getElementById("reportContainer");
    reportContainer.innerHTML = ""; // Clear previous data

    if (Object.keys(data).length === 0) {
        reportContainer.innerHTML = "<p>No data available for the selected time range.</p>";
        return;
    }

    let tableHTML = `<table border="1">
        <thead>
            <tr>
                <th>Category</th>
                <th>Question</th>
                <th>Option</th>
                <th>Count</th>
            </tr>
        </thead>
        <tbody>`;

    for (const category in data) {
        for (const question in data[category]) {
            for (const option in data[category][question]) {
                tableHTML += `
                    <tr>
                        <td>${category}</td>
                        <td>${question}</td>
                        <td>${option}</td>
                        <td>${data[category][question][option]}</td>
                    </tr>
                `;
            }
        }
    }

    tableHTML += `</tbody></table>`;
    reportContainer.innerHTML = tableHTML;
}

document.getElementById("download").addEventListener("click", function() {
    if (data) {
        downloadCSV(data);  
    } else {
        alert("No report data available to download.");
    }
});

function downloadCSV(data) {
    if (!data || Object.keys(data).length === 0) {
        alert("No data available to download.");
        return;
    }

    let csvContent = "data:text/csv;charset=utf-8,";

    // Add CSV headers
    csvContent += "Category,Question,Option,Count\n";

    // Loop through data and format it as CSV
    for (const category in data) {
        for (const question in data[category]) {
            for (const option in data[category][question]) {
                const count = data[category][question][option];
                csvContent += `"${category}","${question}","${option}",${count}\n`;
            }
        }
    }

    // Encode and create a downloadable link
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", "Employee_Wellness_Report.csv");
    document.body.appendChild(link);

    // Trigger download
    link.click();
    document.body.removeChild(link);
}


