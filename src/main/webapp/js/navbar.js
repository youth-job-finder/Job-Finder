document.addEventListener("DOMContentLoaded", function() {
    const toggleBtn = document.querySelector(".menu-toggle");
    const navLinks = document.querySelector(".nav-links");

    toggleBtn.addEventListener("click", function() {
        navLinks.classList.toggle("show");
    });
});