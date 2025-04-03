document.addEventListener("DOMContentLoaded", function () {
    const logoutBtn = document.getElementById("logout");

    if (logout) {
        logout.addEventListener("click", function () {
         
            // Remove JWT token from localStorage
            localStorage.removeItem("jwt");

            // Redirect to login page
            window.location.href = "/index.html"; // Change to your login page
        });
    }
});
