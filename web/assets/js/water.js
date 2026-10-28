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
        const tbody = document.querySelector('#waterTable tbody');
        if (!tbody) return;
        if (!Array.isArray(records) || records.length === 0) {
            tbody.innerHTML = E.emptyRow(7, 'No water sources have been recorded.');
            return;
        }
        tbody.innerHTML = records.map(function (record, index) {
            return '<tr>'
                + '<td>' + (index + 1) + '</td>'
                + '<td><strong>' + E.escapeHtml(record.campName) + '</strong></td>'
                + '<td>' + E.escapeHtml(record.sourceType) + '</td>'
                + '<td>' + E.formatNumber(record.capacity) + ' L</td>'
                + '<td>' + E.formatDate(record.lastInspected) + '</td>'
                + '<td><span class="badge ' + E.badgeClass(record.condition) + '">' + E.escapeHtml(record.condition) + '</span></td>'
                + '<td class="actions-cell">' + E.deleteForm('WaterServlet', record.id, 'water source') + '</td>'
                + '</tr>';
        }).join('');
    }

    async function load() {
        const tbody = document.querySelector('#waterTable tbody');
        try {
            if (tbody) tbody.innerHTML = E.emptyRow(7, 'Loading water sources…');
            const data = await E.fetchJSON('GetCampsServlet');
            populateCamps(Array.isArray(data) ? data : []);
            render(await E.fetchJSON('GetWaterServlet'));
        } catch (error) {
            if (tbody) tbody.innerHTML = E.emptyRow(7, error.message || 'Unable to load water sources.');
        }
    }

    document.addEventListener('DOMContentLoaded', load);
})();
