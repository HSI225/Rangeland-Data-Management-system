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
        const tbody = document.querySelector('#grazingTable tbody');
        if (!tbody) return;
        if (!Array.isArray(records) || records.length === 0) {
            tbody.innerHTML = E.emptyRow(8, 'No grazing rotations have been recorded.');
            return;
        }
        tbody.innerHTML = records.map(function (record, index) {
            return '<tr>'
                + '<td>' + (index + 1) + '</td>'
                + '<td><strong>' + E.escapeHtml(record.campName) + '</strong></td>'
                + '<td>' + E.escapeHtml(record.livestockType) + '</td>'
                + '<td>' + E.formatNumber(record.count) + '</td>'
                + '<td>' + E.formatDate(record.startDate) + '</td>'
                + '<td>' + E.formatDate(record.endDate) + '</td>'
                + '<td>' + (record.notes ? E.escapeHtml(record.notes) : '—') + '</td>'
                + '<td class="actions-cell">' + E.deleteForm('GrazingServlet', record.id, 'grazing record') + '</td>'
                + '</tr>';
        }).join('');
    }

    async function load() {
        const tbody = document.querySelector('#grazingTable tbody');
        try {
            if (tbody) tbody.innerHTML = E.emptyRow(8, 'Loading grazing rotations…');
            const data = await E.fetchJSON('GetCampsServlet');
            populateCamps(Array.isArray(data) ? data : []);
            render(await E.fetchJSON('GetGrazingServlet'));
        } catch (error) {
            if (tbody) tbody.innerHTML = E.emptyRow(8, error.message || 'Unable to load grazing rotations.');
        }
    }

    document.addEventListener('DOMContentLoaded', load);
})();
