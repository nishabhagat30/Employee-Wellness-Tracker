
const token = localStorage.getItem("jwt");
/******************************************* Available Surveys *******************************************************/
function fetchAvailableSurveys() {
    console.log("fetchAvailableSurveys() function called."); 
    const availableSurveysTable = document.getElementById("availableSurveysTable").getElementsByTagName('tbody')[0];
    const token = localStorage.getItem('jwt');  // Get token from localStorage

    if (!token) {
        alert('You are not authenticated!');
        return;
    }

    fetch('/survey-templates/available', {
        method: 'GET',
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        },
    })
    .then(response => {
        if (!response.ok) {
            if (response.status === 403) {
                alert('Access Denied: You do not have permission to view this resource.');
            }
            throw new Error('Failed to fetch surveys: ' + response.statusText);
        }
        return response.json();
    })
    .then(data => { 
        if (data.length > 0) {
            data.forEach(survey => {
                const row = availableSurveysTable.insertRow();
                
                // Insert Survey Name
                row.insertCell(0).innerText = survey.templateName;

                // Insert Survey Category
                row.insertCell(1).innerText = survey.category;

                // Create "Fill Survey" button and add event listener
                const actionsCell = row.insertCell(2);
                const fillSurveyBtn = document.createElement("button");
                fillSurveyBtn.innerText = "Fill Survey";
                fillSurveyBtn.addEventListener("click", () => fillSurvey(survey.templateId));
                actionsCell.appendChild(fillSurveyBtn);
            });
        } else {
            availableSurveysTable.innerHTML = "<tr><td colspan='3'>No available surveys</td></tr>";
        }
    })
    .catch(err => {
        console.error("Error fetching surveys:", err);
        availableSurveysTable.innerHTML = "<tr><td colspan='3'>Error fetching surveys: " + err.message + "</td></tr>";
    });
}


//--------------------------Fill Survey -----------------------------------//

function fillSurvey(templateId) {
    if (!templateId) {
        console.error("Survey ID is missing!");
        return;
    }

    console.log(`Redirecting to survey.html with surveyId=${templateId}`);
    window.location.href = `survey.html?templateId=${templateId}`;
}


/*********************************************** Past Submissions *************************************************************/
async function fetchPastSubmissions() {
    console.log("Fetching past submission");
    const pastSubmissionsTable = document.getElementById("pastSubmissionsTable").getElementsByTagName("tbody")[0];

    if (!token) {
        alert("Authentication required. Redirecting to login.");
        window.location.href = "../index.html";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/survey-responses/user-submissions", {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        });
       
        console.log("Response Status:", response.status); // Debugging
        if (!response.ok) {
            if (response.status === 401) {
                alert("Session expired. Please log in again.");
                localStorage.removeItem("jwt");
                window.location.href = "../index.html";
            } else if (response.status === 403) {
                alert("Access Denied: You do not have permission.");
            } else {
                alert(`Error: ${response.statusText}`);
            }
            throw new Error(`HTTP Error: ${response.status}`);
        }

        const data = await response.json();
       
        // Clear previous entries
        pastSubmissionsTable.innerHTML = "";

        if (Array.isArray(data) && data.length > 0) {
            data.forEach(submission => {
                console.log("Submission Object:", submission);

                const row = pastSubmissionsTable.insertRow();
                
                row.insertCell(0).innerText = submission.templateName || "N/A";
                row.insertCell(1).innerText = submission.category || "N/A";
                row.insertCell(2).innerText = new Date(submission.submittedAt).toLocaleString() || "N/A";
                
                // Actions Cell
                const actionsCell = row.insertCell(3);

                // Update Button (Replaces "View Details")
                const updateBtn = document.createElement("button");
                updateBtn.innerText = "Update";
                updateBtn.onclick = () => updateSubmission(submission.responseId);
                actionsCell.appendChild(updateBtn);

                // Delete Button
                const deleteBtn = document.createElement("button");
                deleteBtn.innerText = "Delete";
                deleteBtn.onclick = () => deleteSubmission(submission.responseId);
                deleteBtn.style.marginLeft = "5px"; // Add spacing
                actionsCell.appendChild(deleteBtn);
            });
        } else {
            pastSubmissionsTable.innerHTML = "<tr><td colspan='5'>No past submissions found</td></tr>";
        }
    } catch (error) {
        console.error("Error fetching past submissions:", error);
        pastSubmissionsTable.innerHTML = `<tr><td colspan='5'>Error: ${error.message}</td></tr>`;
    }
}

function checkEditableStatus(responseData) {
    const editableTill = new Date(responseData.editableTill);
    const currentTime = new Date();

    if (currentTime > editableTill) {
        document.querySelector("#updateButton").disabled = true;
        alert("Editing time has expired!");
    }
}

function updateSubmission(responseId) {
    fetch(`http://localhost:8080/survey-responses/${responseId}`, {
        method: "GET",
        headers: { "Authorization": `Bearer ${token}` }
    })
    .then(response => response.json())
    .then(responseData => {
        checkEditableStatus(responseData);
        if (new Date() <= new Date(responseData.editableUntil)) {
            window.location.href = `updateresponse.html?responseId=${responseId}`;
        } else {
            alert("Editing time has expired! You cannot update this response.");
        }
    })
    .catch(error => console.error("Error fetching survey response:", error));
}

//------------------------Delete Submission----------------------------------//
function deleteSubmission(responseId) {
    fetch(`http://localhost:8080/survey-responses/${responseId}`, {
        method: "GET",
        headers: { "Authorization": `Bearer ${token}` }
    })
    .then(response => response.json())
    .then(responseData => {
        checkEditableStatus(responseData);
        if (new Date() <= new Date(responseData.editableUntil)) {
            confirmDelete(responseId);
        } else {
            alert("Editing time has expired! You cannot delete this response.");
        }
    })
    .catch(error => console.error("Error fetching survey response:", error));
}

function confirmDelete(responseId) { 

    fetch(`http://localhost:8080/survey-responses/delete/${responseId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
           "Authorization": `Bearer ${token}`,
        }
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(`Failed to delete survey response: ${text}`);
            });
        }
        alert("Survey response deleted successfully.");
        location.reload();  // Reload the page to update the list of responses
    })
    .catch(error => {
        console.error("Error deleting survey response:", error);
        alert("Error deleting survey response. Please try again later.");
    });
}

/*****************************************************deactivate Account*********************************************************************/
document.getElementById("deactivateBtn").addEventListener("click", async function () {
    try {
        const response = await fetch("http://localhost:8080/users/deactivate", {
            method: "PUT",
            headers: { "Authorization": `Bearer ${token}` 
        }
        });

        if (!response.ok) {
            const errorMessage = await response.text();
            alert(errorMessage); // Show backend message
            return;
        }

        alert("Your account has been deactivated. Logging out...");
        localStorage.clear(); // Remove JWT and session data
        window.location.href = "/index.html"; //Redirect to login page

    } catch (error) {
        alert("Error deactivating account. Please try again.");
    }
});

/******************************************************* Update Profile **************************************************** */
document.addEventListener("DOMContentLoaded", function () {
    const updateProfilePopup = document.getElementById("updateProfilePopup");
    const closePopup = document.getElementById("closePopup");
    const updateProfileForm = document.getElementById("updateProfileForm");
    const updateMessage = document.getElementById("updateMessage");
    const updateProfileBtn = document.getElementById("updateProfileBtn"); // Ensure button exists

    // Fetch current employee details and fill the form
    function fetchEmployeeDetails() {
        fetch("http://localhost:8080/users/profile", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`,
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch user details");
            }
            return response.json();
        })
        .then(data => {
            document.getElementById("name").value = data.name;
            document.getElementById("email").value = data.email;
        })
        .catch(error => console.error("Error fetching user details:", error));
    }

    updateProfileBtn.addEventListener("click", () => {
        showUpdatePopup();
    });

    // Show popup and load employee details
    function showUpdatePopup() {
        fetchEmployeeDetails();
        updateProfilePopup.style.display = "block";
    }

    // Hide popup when clicking "×" button
    closePopup.addEventListener("click", function () {
        updateProfilePopup.style.display = "none";
    });

    // Close popup when clicking outside the modal
    window.addEventListener("click", function (event) {
        if (event.target === updateProfilePopup) {
            updateProfilePopup.style.display = "none";
        }
    });

    // // Handle profile update submission
    updateProfileForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const updatedData = {
            name: document.getElementById("name").value,
            email: document.getElementById("email").value,
            password: document.getElementById("password").value || null
        };

        fetch("http://localhost:8080/users", {  
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`,
            },
            body: JSON.stringify(updatedData)
        })

        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to update profile");
            }
            return response.json(); // ✅ Parse response as JSON
        })
        .then(data => {
            if (data.message) { //  Display only the message field
                updateMessage.innerText = data.message; 
            } else {
                updateMessage.innerText = "Profile updated successfully!"; // Fallback message
            }
            setTimeout(() => {
                updateProfilePopup.style.display = "none"; 
                window.location.href = "/index.html"; // Redirect to login after update
            }, 2000);
        })
        
        .catch(error => console.error("Error updating profile:", error));
    });

   
});


 // Initial fetch of surveys and past submissions
fetchAvailableSurveys();
fetchPastSubmissions();
