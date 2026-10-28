(function () {
    'use strict';
    const E = window.Eiseb;

    function render(camps) {
        const tbody = document.querySelector('#campsTable tbody');
        if (!tbody) return;
        if (!Array.isArray(camps) || camps.length === 0) {
            tbody.innerHTML = E.emptyRow(9, 'No camps have been registered yet.');
            return;
        }
        tbody.innerHTML = camps.map(function (camp, index) {
            return '<tr>'
                + '<td>' + (index + 1) + '</td>'
                + '<td><strong>' + E.escapeHtml(camp.name) + '</strong></td>'
                + '<td>' + E.escapeHtml(camp.location) + '</td>'
                + '<td>' + E.formatNumber(camp.size) + ' ha</td>'
                + '<td>' + E.formatNumber(camp.capacity) + '</td>'
                + '<td>' + E.escapeHtml(camp.type) + '</td>'
                + '<td>' + E.escapeHtml(camp.water) + '</td>'
                + '<td><span class="badge ' + E.badgeClass(camp.status || 'Active') + '">' + E.escapeHtml(camp.status || 'Active') + '</span></td>'
                + '<td class="actions-cell">' + E.deleteForm('CampServlet', camp.id, 'camp') + '</td>'
                + '</tr>';
        }).join('');
    }

    async function load() {
        const tbody = document.querySelector('#campsTable tbody');
        try {
            if (tbody) tbody.innerHTML = E.emptyRow(9, 'Loading camps…');
            render(await E.fetchJSON('GetCampsServlet'));
        } catch (error) {
            if (tbody) tbody.innerHTML = E.emptyRow(9, error.message || 'Unable to load camps.');
        }
    }

    document.addEventListener('DOMContentLoaded', load);
})();
