
let responseId;
let token; 

document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    responseId = urlParams.get("responseId"); // Get survey ID from URL
    token = localStorage.getItem("jwt"); 
    const formContainer = document.querySelector("#surveyForm");

    if (!responseId || !token) {
        alert("Invalid request. Missing response ID or authentication.");
        return;
    }

    async function fetchSurveyResponse() {
        try {
            const res = await fetch(`http://localhost:8080/survey-responses/${responseId}`, {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            });

            if (!res.ok) throw new Error("Failed to fetch survey response");

            const responseData = await res.json();
            populateForm(responseData);
        } catch (error) {
            console.error("Error fetching survey response:", error);
            alert("Error loading survey data.");
        }
    }

    function populateForm(responseData) {
        formContainer.innerHTML = `
        <h2>${responseData.surveyTemplate.templateName} - ${responseData.surveyTemplate.category}</h2>
        <form id="responseForm"></form>`;

        const responseForm = document.querySelector("#responseForm");

        responseData.answers.forEach((answer,index) => {
            const questionId = answer.question.questionId;
            const answerText = answer.answerText;

            const questionDiv = document.createElement("div");
            questionDiv.innerHTML = `<p><strong>Q${index + 1}:</strong> ${answer.question.questionText}</p>`;
            ["option1", "option2", "option3", "option4"].forEach((option) => {
                const label = document.createElement("label");
                const input = document.createElement("input");

                input.type = "radio";
                input.name = `q${questionId}`;
                input.value = answer.question[option];
                if (answerText === answer.question[option]) {
                    input.checked = true;
                }

                label.appendChild(input);
                label.append(` ${answer.question[option]}`);
                questionDiv.appendChild(label);
                questionDiv.appendChild(document.createElement("br"));
            });

            responseForm.appendChild(questionDiv);
        });

        responseForm.innerHTML += `<button type="submit">Update Response</button>`;
        responseForm.addEventListener("submit", submitUpdatedResponse); // Attach event listener correctly
    }

    fetchSurveyResponse();
});

async function submitUpdatedResponse(event) {
    event.preventDefault(); 

    const updatedAnswers = [];
    document.querySelectorAll("input[type='radio']:checked").forEach((input) => {
        const questionId = input.name.replace("q", "");
        updatedAnswers.push({
            question: { questionId: parseInt(questionId) },
            answerText: input.value
        });
    });

    const updatedResponse = {
        responseId: responseId, // Ensure response ID is sent
        answers: updatedAnswers
    };

    try {
        const res = await fetch(`http://localhost:8080/survey-responses/${responseId}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedResponse)
        });

        if (res.ok) {
            alert("Survey response updated successfully!");
            window.location.href = "../pages/employeedashboard.html"; // Redirect after success
        } else {
            alert("Error updating response.");
        }
    } catch (error) {
        console.error("Error updating response:", error);
        alert("Could not update survey response.");
    }
}


