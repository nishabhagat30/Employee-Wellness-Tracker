document.getElementById("registerForm").addEventListener("submit", async function (event) {
    event.preventDefault(); // Prevent page reload

    
    const name = document.getElementById("name")?.value || "";
    const email = document.getElementById("email")?.value || "";
    const password = document.getElementById("password")?.value || "";
    //const role = "EMPLOYEE"; // Default role

    if ( !name || !email || !password) {
        document.getElementById("error-message").innerText = "All fields are required!";
        return;
    }

    function validateEmail(email) {
        const allowedDomain = "@example.com"; // ✅ Replace with your company's domain
        return email.endsWith(allowedDomain);
    }

    if (!validateEmail(email)) {
        alert("Please use your company email ending");
        return;
    }

    // ✅ Call handleRegister function
    handleRegister(name, email, password);
});

    async function handleRegister(name, email, password) {
    try{
        
    let response = await fetch("http://localhost:8080/public/register",{
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({name, email, password})
    });

    if (!response.ok) {
        throw new Error("Registration failed");
    }

    alert("Registration successful! Redirecting to login...");
    window.location.href = "/index.html";
} catch (error) {
    document.getElementById("error-message").innerText = "Registration failed. Try again.";
}
}


