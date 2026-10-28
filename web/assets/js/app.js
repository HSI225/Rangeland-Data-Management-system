(function () {
    'use strict';

    const MESSAGES = {
        added: ['success', 'Record added successfully.'],
        deleted: ['success', 'Record deleted successfully.'],
        'in-use': ['warning', 'This record is linked to other records and cannot be deleted.'],
        validation: ['error', 'Please check the highlighted values and try again.'],
        dates: ['error', 'The end date cannot be earlier than the start date.'],
        database: ['error', 'A system error occurred while saving the record. Please try again.'],
        invalid: ['error', 'Invalid username or password.'],
        empty: ['error', 'Please complete all required fields.'],
        exists: ['error', 'That username already exists.'],
        mismatch: ['error', 'The passwords do not match.'],
        weak: ['error', 'Use a password of at least eight characters.'],
        username: ['error', 'Use a username of 3-30 letters, numbers, dots, underscores or hyphens.'],
        connection: ['error', 'Cannot reach the database. Check that MySQL is running, the eiseb_rangeland database exists, and the DB user/password are correct (see the GlassFish server log).'],
        failed: ['error', 'The account could not be saved. Please try again or check the server log.'],
        registered: ['success', 'Registration successful. Please sign in.']
    };

    function params() {
        return new URLSearchParams(window.location.search);
    }

    function showAlert(target, type, message) {
        if (!target) return;
        target.className = 'alert alert-' + type;
        target.textContent = message;
        target.hidden = false;
        target.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }

    function renderQueryAlert() {
        document.querySelectorAll('[data-alert-from-query]').forEach(function (target) {
            const query = params();
            const key = query.get('error') || query.get('success') || (query.get('registered') === 'true' ? 'registered' : '');
            if (!key || !MESSAGES[key]) return;
            showAlert(target, MESSAGES[key][0], MESSAGES[key][1]);
        });
    }

    function setActiveNavigation() {
        const current = window.location.pathname.split('/').pop() || 'dashboard.jsp';
        document.querySelectorAll('.sidebar-nav a[href]').forEach(function (link) {
            const target = link.getAttribute('href').split('/').pop();
            link.classList.toggle('active', target === current);
        });
    }

    function bindPanels() {
        document.querySelectorAll('[data-toggle-panel]').forEach(function (button) {
            button.addEventListener('click', function () {
                const panel = document.querySelector(button.getAttribute('data-toggle-panel'));
                if (!panel) return;
                panel.hidden = !panel.hidden;
                const first = panel.querySelector('input, select, textarea, button');
                if (!panel.hidden && first) first.focus();
            });
        });
    }

    function bindConfirmations() {
        document.querySelectorAll('form[data-confirm]').forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!window.confirm(form.getAttribute('data-confirm'))) {
                    event.preventDefault();
                }
            });
        });
    }

    function escapeHtml(value) {
        if (value === null || value === undefined) return '';
        return String(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    function formatDate(value) {
        if (!value) return '—';
        const date = new Date(value + 'T00:00:00');
        if (Number.isNaN(date.getTime())) return escapeHtml(value);
        return date.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
    }

    function formatNumber(value, decimals) {
        const number = Number(value);
        if (!Number.isFinite(number)) return '—';
        return number.toLocaleString(undefined, {
            minimumFractionDigits: decimals || 0,
            maximumFractionDigits: decimals === undefined ? 2 : decimals
        });
    }

    function titleCase(value) {
        if (!value) return '—';
        return String(value).replace(/\w\S*/g, function (text) {
            return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();
        });
    }

    function badgeClass(value) {
        const key = String(value || '').toLowerCase();
        if (key.includes('good') || key.includes('active')) return 'badge-success';
        if (key.includes('fair') || key.includes('repair')) return 'badge-warning';
        if (key.includes('poor') || key.includes('inactive')) return 'badge-danger';
        return 'badge-neutral';
    }

    async function fetchJSON(url, options) {
        const response = await fetch(url, options);
        let payload = null;
        try {
            payload = await response.json();
        } catch (error) {
            payload = null;
        }
        if (!response.ok) {
            const message = payload && payload.error ? payload.error : 'Request failed with status ' + response.status;
            throw new Error(message);
        }
        return payload;
    }

    function emptyRow(columns, message) {
        return '<tr><td class="empty-state" colspan="' + columns + '">' + escapeHtml(message) + '</td></tr>';
    }

    function deleteForm(servlet, id, label) {
        return '<form class="delete-form" method="post" action="' + servlet + '" data-confirm="Delete this ' + label + '? This action cannot be undone.">'
            + '<input type="hidden" name="action" value="delete">'
            + '<input type="hidden" name="id" value="' + id + '">'
            + '<button class="btn btn-danger" type="submit">Delete</button>'
            + '</form>';
    }

    function setText(id, value) {
        const node = document.getElementById(id);
        if (node) node.textContent = value;
    }

    document.addEventListener('DOMContentLoaded', function () {
        renderQueryAlert();
        setActiveNavigation();
        bindPanels();
        bindConfirmations();
    });

    window.Eiseb = {
        fetchJSON: fetchJSON,
        escapeHtml: escapeHtml,
        formatDate: formatDate,
        formatNumber: formatNumber,
        titleCase: titleCase,
        badgeClass: badgeClass,
        emptyRow: emptyRow,
        deleteForm: deleteForm,
        setText: setText,
        showAlert: showAlert
    };
})();
