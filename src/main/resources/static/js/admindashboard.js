const token = localStorage.getItem("jwt");
document.addEventListener("DOMContentLoaded", function () {
    fetchSurveyTemplates();
});

/******************************************************Manage Surveys******************************************************************** */
// Fetching Available survey templates
function fetchSurveyTemplates() {
    fetch("http://localhost:8080/survey-templates/available",{
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        }
}) 
        .then(response => response.json())
            .then(surveys => {
            const surveyTableBody = document.querySelector("#surveyTable tbody");
            surveyTableBody.innerHTML = ""; 

            surveys.forEach(survey => {
                const row = document.createElement("tr");

                row.innerHTML = `
                    <td>${survey.templateName}</td>
                    <td>${survey.category}</td>
                    <td>${new Date(survey.createdTime).toLocaleDateString()}</td>
                    <td>
                        <button onclick="updateSurvey(${survey.templateId})">Update</button>
                        <button onclick="deleteSurvey(${survey.templateId})">Delete</button>
                    </td>
                `;

                surveyTableBody.appendChild(row);
            });
        })
        .catch(error => console.error("Error fetching surveys:", error));
}


// Function to delete a survey
function deleteSurvey(surveyId) {
    if (confirm("Are you sure you want to delete this survey?")) {
        fetch(`http://localhost:8080/survey-templates/${surveyId}`, {
            method: "DELETE",
            headers: {
                "Authorization": "Bearer " + token,
            }
        })
        .then(response => {
            if (response.ok) {
                alert("Survey deleted successfully.");
                fetchSurveyTemplates(); 
            } else {
                alert("Failed to delete survey.");
            }
        })
        .catch(error => console.error("Error deleting survey:", error));
    }
}

//Function to update a survey 
function updateSurvey(surveyId) {
    window.location.href = `updateSurvey.html?id=${surveyId}`;
}

document.getElementById("createsurvey").addEventListener('click', function(){
      window.location.href = "../pages/createsurvey.html";
})

document.addEventListener("DOMContentLoaded", function () {
    fetchEmployees();
});


//**********************************************************Manage Employeee***************************************************************
// Fetch employees from API
function fetchEmployees() {
    fetch("http://localhost:8080/admin/all-users", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`, 
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => populateEmployeeTable(data))
        .catch(error => console.error("Error fetching employees:", error));
}

// Populate employee table dynamically
function populateEmployeeTable(employees) {
    const tableBody = document.querySelector("#employeeTable tbody");
    tableBody.innerHTML = ""; 

    employees.forEach(employee => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${employee.name}</td>
            <td>${employee.email}</td>
            <td>${employee.role}</td>
            <td>
                <button onclick="updateEmployee(${employee.userId})">Update Role</button>
                <button onclick="deleteEmployee(${employee.userId})">Delete</button>
            </td>
        `;

        tableBody.appendChild(row);
    });
}

document.getElementById("addemployee").addEventListener('click', function(){
    window.location.href = "../pages/addemployee.html";
})

// Function to delete an employee
function deleteEmployee(employeeId) {
    if (!confirm("Are you sure you want to delete this employee?")) {
        return;
    }

    fetch(`http://localhost:8080/admin/${employeeId}`, {
        method: "DELETE",
        headers: {
        "Authorization": `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`Failed to delete employee. Status: ${response.status}`);
        }
        alert("Employee deleted successfully!");
        fetchEmployees(); 
    })
    .catch(error => console.error("Error deleting employee:", error));
}

// Function to update an employee 
function updateEmployee(employeeId) {
    if (!confirm("Are you sure you want to update the role to Admin?")) {
        return;
    }

    fetch(`http://localhost:8080/admin/${employeeId}/role`, {
        method: "PUT",
        headers: {
        "Authorization": `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        return response.text();
    })
    .then(message => {
        alert(message); 
        fetchEmployees(); 
    })
    .catch(error => {
        console.error("Error updating role:", error);
        alert("Failed to update role.");
    });
}
  
/****************************************************Report Generate *********************************************************** */
function generateSurveyReport() {
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;

    if (!startDate || !endDate) {
        alert("Please select both start and end dates.");
        return;
    }

    // Redirect to the survey report page with query parameters
    window.location.href = `report.html?startDate=${startDate}&endDate=${endDate}`;
}
