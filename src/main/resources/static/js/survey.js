
let userId = null;
const templateId = getTemplateIdFromURL();
const token = localStorage.getItem("jwt"); // Get JWT from local storage
function getEmailFromToken() {
    if (!token) {
        console.error("No JWT token found.");
        return null;
    }

    try {
        const payload = JSON.parse(atob(token.split(".")[1])); 
        return payload.sub; 
    } catch (error) {
        console.error("Error decoding JWT token:", error);
        return null;
    }
}


const email = getEmailFromToken();
if (email) {
    console.log("User Email from JWT:", email);
} else {
    console.error("Failed to retrieve email from token.");
}


async function getUserIdByEmail(email) {
    try {
        const response = await fetch(`http://localhost:8080/users/email/${email}`, {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!response.ok) throw new Error("Failed to fetch user ID");

        const userData = await response.json();
        return userData.userId;
    } catch (error) {
        console.error("Error fetching user ID:", error);
        return null;
    }
}

// Assign userId
async function setUserId() {
    const email = getEmailFromToken();
    if (!email) {
        console.error(" Email not found in token.");
        return;
    }

    userId = await getUserIdByEmail(email);
    console.log(" Retrieved User ID:", userId);
}


setUserId();

function getTemplateIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    const templateId = urlParams.get("templateId");

    if (!templateId) {
        console.error("Template ID not found in URL.");
        return null;
    }
    
    return parseInt(templateId, 10); // Convert to integer
}


document.addEventListener("DOMContentLoaded", fetchQuestions);

async function fetchQuestions() {
    try {
        const response = await fetch(`http://localhost:8080/survey-templates/${templateId}`, {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` ,
                        "content-type":"application/json" 
        }
    });
        const data = await response.json();

        const questionsContainer = document.getElementById("questionsContainer");
        questionsContainer.innerHTML = "";

        data.questions.forEach((question,index) => {
            const questionDiv = document.createElement("div");
            questionDiv.classList.add("question");

            questionDiv.innerHTML = `
                <p><strong>Q${index + 1}:</strong>${question.questionText}</p>
                <label><input type="radio" name="q${question.questionId}" value="${question.option1}"> ${question.option1}</label><br>
                <label><input type="radio" name="q${question.questionId}" value="${question.option2}"> ${question.option2}</label><br>
                <label><input type="radio" name="q${question.questionId}" value="${question.option3}"> ${question.option3}</label><br>
                <label><input type="radio" name="q${question.questionId}" value="${question.option4}"> ${question.option4}</label>
            `;

            questionsContainer.appendChild(questionDiv);
        });
    } catch (error) {
        console.error("Error fetching questions:", error);
    }
}

document.getElementById("surveyForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    // Ensure userId is retrieved before proceeding
    if (!userId) {
        await setUserId();  // Wait for userId to be set
    }

    if (!userId) {
        console.error("User ID is missing. Cannot submit survey.");
        return;
    }

    const answers = [];
    const questionElements = document.querySelectorAll(".question");

    questionElements.forEach(questionDiv => {
        const questionId = parseInt(questionDiv.querySelector("input").name.substring(1));
        const selectedOption = questionDiv.querySelector("input:checked");

        if (selectedOption) {
            answers.push({
                questionId: questionId,
                answerText: selectedOption.value
            });
        }
    });

    const surveyResponse = {
        userId: userId,
        templateId: templateId,
        answers: answers
    };

    try {
        const response = await fetch("http://localhost:8080/survey-responses/submit", {
            method: "POST",
            headers: { 
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json" 
            },
            body: JSON.stringify(surveyResponse)
        });

        const contentType = response.headers.get("content-type");
        let message = "Survey submitted successfully!"; // Default message

        if (contentType && contentType.includes("application/json")) {
            try {
                const result = await response.json();
                message = result.message || message;
            } catch (error) {
                console.error("Error parsing JSON:", error);
            }
        } else {
            try {
                const textResponse = await response.text(); // Read plain text response
                message = textResponse || message;
            } catch (error) {
                console.error("Error parsing text:", error);
            }
        }

        alert(message); // Show the response message
        window.location.href = "employeedashboard.html"; // Redirect after alert

    } catch (error) {
        console.error("Error submitting survey:", error);
        alert("Error submitting survey. Please try again.");
    }
});







