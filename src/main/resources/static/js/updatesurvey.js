const urlParams = new URLSearchParams(window.location.search);
const surveyId = urlParams.get("id"); // Get survey ID from URL
const token = localStorage.getItem("jwt");

let questions = [];

// Fetch existing survey data
function loadSurveyData() {
    fetch(`http://localhost:8080/survey-templates/${surveyId}`,{
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        }
   })
        .then(response => response.json())
        .then(data => {
            document.getElementById("templateName").value = data.templateName;
            document.getElementById("category").value = data.category;

            data.questions.forEach((question, index) => {
                addQuestion(question);
            });
        })
        .catch(error => console.error("Error fetching survey:", error));
}

// Dynamically add questions with pre-filled data
function addQuestion(existingQuestion = null) {
    if (questions.length >= 5) {
        alert("Maximum 5 questions allowed!");
        return;
    }

    const questionIndex = questions.length;
    const questionDiv = document.createElement("div");
    questionDiv.classList.add("question");
    questionDiv.innerHTML = `
        <label>Question ${questionIndex + 1}:</label>
        <input type="text" class="questionText" required value="${existingQuestion ? existingQuestion.questionText : ''}">

        <label>Option 1:</label>
        <input type="text" class="option1" required value="${existingQuestion ? existingQuestion.option1 : ''}">
        <label>Option 2:</label>
        <input type="text" class="option2" required value="${existingQuestion ? existingQuestion.option2 : ''}">
        <label>Option 3:</label>
        <input type="text" class="option3" required value="${existingQuestion ? existingQuestion.option3 : ''}">
        <label>Option 4:</label>
        <input type="text" class="option4" required value="${existingQuestion ? existingQuestion.option4 : ''}">

        <button type="button" onclick="removeQuestion(this)">Remove</button>
        <hr>
    `;
    document.getElementById("questionsContainer").appendChild(questionDiv);
    questions.push(questionDiv);
}

// Remove question
function removeQuestion(button) {
    const questionDiv = button.parentElement;
    questionDiv.remove();
    questions = questions.filter(q => q !== questionDiv);
}

// Handle form submission
document.getElementById("updateSurveyForm").addEventListener("submit", function (event) {
    event.preventDefault();

    const surveyData = {
        templateName: document.getElementById("templateName").value,
        category: document.getElementById("category").value,
        questions: []
    };

    questions.forEach(q => {
        surveyData.questions.push({
            questionText: q.querySelector(".questionText").value,
            option1: q.querySelector(".option1").value,
            option2: q.querySelector(".option2").value,
            option3: q.querySelector(".option3").value,
            option4: q.querySelector(".option4").value
        });
    });

fetch(`http://localhost:8080/survey-templates/${surveyId}`, {
    method: "PUT",
    headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
    },
    body: JSON.stringify(surveyData)
})
.then(response => response.text()) 
.then(text => {
    console.log("Server Response:", text); 
    
    if (text.includes("Survey template updated successfully")) { 
        alert("Survey Updated Successfully!");
        window.location.href = "admindashboard.html";
    } else {
        throw new Error(text); 
    }
})
.catch(error => {
    console.error("Error:", error);
    alert(error.message);
});

});

// Load survey data on page load
window.onload = loadSurveyData;


