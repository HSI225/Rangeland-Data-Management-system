(function () {
    'use strict';
    const E = window.Eiseb;

    function populateCamps(camps) {
        const select = document.getElementById('camp_id');
        if (!select) return;
        select.innerHTML = '<option value="">Select camp</option>' + camps.map(function (camp) {
            return '<option value="' + camp.id + '">' + E.escapeHtml(camp.name) + '</option>';
        }).join('');
    }

    function render(records) {
        const tbody = document.querySelector('#monitoringTable tbody');
        if (!tbody) return;
        if (!Array.isArray(records) || records.length === 0) {
            tbody.innerHTML = E.emptyRow(8, 'No vegetation monitoring records are available.');
            return;
        }
        tbody.innerHTML = records.map(function (record, index) {
            return '<tr>'
                + '<td>' + (index + 1) + '</td>'
                + '<td><strong>' + E.escapeHtml(record.campName) + '</strong></td>'
                + '<td>' + E.formatDate(record.date) + '</td>'
                + '<td><span class="badge ' + E.badgeClass(record.cover) + '">' + E.escapeHtml(record.cover) + '</span></td>'
                + '<td>' + E.formatNumber(record.grassHeight, 1) + ' cm</td>'
                + '<td>' + E.escapeHtml(record.recordedBy) + '</td>'
                + '<td>' + (record.notes ? E.escapeHtml(record.notes) : '—') + '</td>'
                + '<td class="actions-cell">' + E.deleteForm('MonitoringServlet', record.id, 'monitoring record') + '</td>'
                + '</tr>';
        }).join('');
    }

    async function load() {
        const tbody = document.querySelector('#monitoringTable tbody');
        try {
            if (tbody) tbody.innerHTML = E.emptyRow(8, 'Loading monitoring records…');
            const data = await E.fetchJSON('GetCampsServlet');
            populateCamps(Array.isArray(data) ? data : []);
            render(await E.fetchJSON('GetMonitoringServlet'));
        } catch (error) {
            if (tbody) tbody.innerHTML = E.emptyRow(8, error.message || 'Unable to load monitoring records.');
        }
    }

    document.addEventListener('DOMContentLoaded', load);
})();
