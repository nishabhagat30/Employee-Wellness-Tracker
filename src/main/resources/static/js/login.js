document.getElementById("loginForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    if (!email || !password) {
        alert("Please enter both email and password.");
        return;
    }

    const response = await fetch("http://localhost:8080/public/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });

    if (response.ok) {
        const token = await response.text(); //  Read JWT token from response
        localStorage.setItem("jwt", token); //  Store JWT in localStorage

        // Decode JWT to extract roles
        const payloadBase64 = token.split(".")[1]; // Extract payload part
        const decodedPayload = JSON.parse(atob(payloadBase64)); // Decode Base64

        if (decodedPayload.roles.includes("ADMIN")) {
            window.location.href = "../pages/admindashboard.html";
        } else {
            window.location.href = "../pages/employeedashboard.html";
        }
    } else {
        alert("Login failed!");
    }
});




