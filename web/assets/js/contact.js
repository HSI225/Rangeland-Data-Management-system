(function () {
    'use strict';
    document.addEventListener('DOMContentLoaded', function () {
        const form = document.getElementById('contactForm');
        const status = document.getElementById('contactStatus');
        if (!form || !status) return;
        form.addEventListener('submit', function (event) {
            event.preventDefault();
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }
            form.reset();
            status.hidden = false;
            status.className = 'alert alert-success';
            status.textContent = 'Thank you. Your message has been prepared for the support team.';
        });
    });
})();
