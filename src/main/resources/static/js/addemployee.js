document.getElementById("addEmployeeForm").addEventListener("submit", async function (event) {
    event.preventDefault(); // Prevent page reload
 
    const name = document.getElementById("name")?.value || "";
    const email = document.getElementById("email")?.value || "";
    const password = document.getElementById("password")?.value || "";
    const role = document.getElementById("role")?.value.toUpperCase() || "";

    if ( !name || !email || !password || !role) {
        document.getElementById("error-message").innerText = "All fields are required!";
        return;
    }

    try{
    let response = await fetch("http://localhost:8080/admin/add",{
        method: "POST",
        headers: { 
            "Authorization" : `Bearer ${localStorage.getItem("jwt")}`,
            "Content-Type": "application/json" },
             body: JSON.stringify({name, email, password, role})
    });

    if (!response.ok) {
        throw new Error("Registration failed");
    }

    alert("Registration successful!");
    window.location.href = "../pages/admindashboard.html";
} catch (error) {
    alert("Registration failed");
}
});


