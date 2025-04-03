let questions = [];

function addQuestion() {
    if (questions.length >= 5) {
        alert("Maximum 5 questions allowed!");
        return;
    }

    const questionIndex = questions.length;
    const questionDiv = document.createElement("div");
    questionDiv.classList.add("question");
    questionDiv.innerHTML = `
        <label>Question ${questionIndex + 1}:</label>
        <input type="text" class="questionText" required>

        <label>Option 1:</label>
        <input type="text" class="option1" required>
        <label>Option 2:</label>
        <input type="text" class="option2" required>
        <label>Option 3:</label>
        <input type="text" class="option3" required>
        <label>Option 4:</label>
        <input type="text" class="option4" required>

        <button type="button" onclick="removeQuestion(this)">Remove</button>
        <hr>
    `;
    document.getElementById("questionsContainer").appendChild(questionDiv);
    questions.push(questionDiv);
}

function removeQuestion(button) {
    const questionDiv = button.parentElement;
    questionDiv.remove();
    questions = questions.filter(q => q !== questionDiv);
}

// Handle form submission
document.getElementById("surveyForm").addEventListener("submit", function (event) {
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

    fetch("http://localhost:8080/survey-templates", {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${localStorage.getItem("jwt")}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(surveyData)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text || "Unknown error"); });
        }
        return response.json();
    })
    .then(data => {
        alert("Survey Created Successfully!");
        window.location.href = "admindashboard.html";
    })
    .catch(error => console.error("Error:", error));
});
