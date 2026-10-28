(function () {
    'use strict';
    const E = window.Eiseb;
    let allData = { camps: [], monitoring: [], grazing: [], water: [] };

    function selectedCamp() { return document.getElementById('filterCamp').value; }
    function fromDate() { return document.getElementById('filterFrom').value; }
    function toDate() { return document.getElementById('filterTo').value; }

    function byCamp(records, field) {
        const selected = selectedCamp();
        if (!selected) return records;
        return records.filter(function (record) { return record[field] === selected; });
    }

    function byDate(records, field) {
        const from = fromDate();
        const to = toDate();
        return records.filter(function (record) {
            const value = record[field] || '';
            if (from && value < from) return false;
            if (to && value > to) return false;
            return true;
        });
    }

    function populateCampFilter(camps) {
        const select = document.getElementById('filterCamp');
        const selected = select.value;
        select.innerHTML = '<option value="">All camps</option>' + camps.map(function (camp) {
            return '<option value="' + E.escapeHtml(camp.name) + '">' + E.escapeHtml(camp.name) + '</option>';
        }).join('');
        select.value = selected;
    }

    function renderCamps(records) {
        const tbody = document.querySelector('#campTable tbody');
        if (!tbody) return;
        if (!records.length) { tbody.innerHTML = E.emptyRow(8, 'No camp records match the selected filters.'); return; }
        tbody.innerHTML = records.map(function (record, index) {
            return '<tr><td>' + (index + 1) + '</td><td>' + E.escapeHtml(record.name) + '</td><td>' + E.escapeHtml(record.location) + '</td><td>' + E.formatNumber(record.size) + ' ha</td><td>' + E.formatNumber(record.capacity) + '</td><td>' + E.escapeHtml(record.type) + '</td><td>' + E.escapeHtml(record.water) + '</td><td>' + E.escapeHtml(record.status || 'Active') + '</td></tr>';
        }).join('');
    }

    function renderMonitoring(records) {
        const tbody = document.querySelector('#monitorTable tbody');
        if (!tbody) return;
        if (!records.length) { tbody.innerHTML = E.emptyRow(7, 'No monitoring records match the selected filters.'); return; }
        tbody.innerHTML = records.map(function (record, index) {
            return '<tr><td>' + (index + 1) + '</td><td>' + E.escapeHtml(record.campName) + '</td><td>' + E.formatDate(record.date) + '</td><td>' + E.escapeHtml(record.cover) + '</td><td>' + E.formatNumber(record.grassHeight, 1) + ' cm</td><td>' + E.escapeHtml(record.recordedBy) + '</td><td>' + (record.notes ? E.escapeHtml(record.notes) : '—') + '</td></tr>';
        }).join('');
    }

    function renderGrazing(records) {
        const tbody = document.querySelector('#grazingTable tbody');
        if (!tbody) return;
        if (!records.length) { tbody.innerHTML = E.emptyRow(7, 'No grazing records match the selected filters.'); return; }
        tbody.innerHTML = records.map(function (record, index) {
            return '<tr><td>' + (index + 1) + '</td><td>' + E.escapeHtml(record.campName) + '</td><td>' + E.escapeHtml(record.livestockType) + '</td><td>' + E.formatNumber(record.count) + '</td><td>' + E.formatDate(record.startDate) + '</td><td>' + E.formatDate(record.endDate) + '</td><td>' + (record.notes ? E.escapeHtml(record.notes) : '—') + '</td></tr>';
        }).join('');
    }

    function renderWater(records) {
        const tbody = document.querySelector('#waterTable tbody');
        if (!tbody) return;
        if (!records.length) { tbody.innerHTML = E.emptyRow(6, 'No water records match the selected filters.'); return; }
        tbody.innerHTML = records.map(function (record, index) {
            return '<tr><td>' + (index + 1) + '</td><td>' + E.escapeHtml(record.campName) + '</td><td>' + E.escapeHtml(record.sourceType) + '</td><td>' + E.formatNumber(record.capacity) + ' L</td><td>' + E.formatDate(record.lastInspected) + '</td><td>' + E.escapeHtml(record.condition) + '</td></tr>';
        }).join('');
    }

    function render() {
        const camps = allData.camps;
        const monitoring = byCamp(byDate(allData.monitoring, 'date'), 'campName');
        const grazing = byCamp(byDate(allData.grazing, 'startDate'), 'campName');
        const water = byCamp(allData.water, 'campName');
        renderCamps(camps);
        renderMonitoring(monitoring);
        renderGrazing(grazing);
        renderWater(water);
        E.setText('reportCount', camps.length + monitoring.length + grazing.length + water.length);
    }

    function switchTab(name) {
        document.querySelectorAll('.tab-btn').forEach(function (button) {
            button.classList.toggle('active', button.getAttribute('data-tab') === name);
        });
        document.querySelectorAll('.report-tab').forEach(function (panel) {
            panel.classList.toggle('active', panel.id === 'tab-' + name);
        });
    }

    function csvValue(value) {
        const text = String(value === null || value === undefined ? '' : value);
        return '"' + text.replace(/"/g, '""') + '"';
    }

    function exportCSV(type) {
        const rows = {
            camps: [['Camp', 'Location', 'Size (ha)', 'Capacity', 'Type', 'Water Source', 'Status']],
            monitoring: [['Camp', 'Date', 'Cover', 'Grass Height (cm)', 'Recorded By', 'Notes']],
            grazing: [['Camp', 'Livestock', 'Count', 'Start', 'End', 'Notes']],
            water: [['Camp', 'Source', 'Capacity (L)', 'Last Inspected', 'Condition']]
        };
        const filters = {
            camps: allData.camps,
            monitoring: byCamp(byDate(allData.monitoring, 'date'), 'campName'),
            grazing: byCamp(byDate(allData.grazing, 'startDate'), 'campName'),
            water: byCamp(allData.water, 'campName')
        };
        const data = filters[type] || [];
        data.forEach(function (record) {
            if (type === 'camps') rows.camps.push([record.name, record.location, record.size, record.capacity, record.type, record.water, record.status || 'Active']);
            if (type === 'monitoring') rows.monitoring.push([record.campName, record.date, record.cover, record.grassHeight, record.recordedBy, record.notes || '']);
            if (type === 'grazing') rows.grazing.push([record.campName, record.livestockType, record.count, record.startDate, record.endDate, record.notes || '']);
            if (type === 'water') rows.water.push([record.campName, record.sourceType, record.capacity, record.lastInspected, record.condition]);
        });
        const csv = rows[type].map(function (row) { return row.map(csvValue).join(','); }).join('\n');
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'eiseb-' + type + '-report.csv';
        document.body.appendChild(link);
        link.click();
        link.remove();
        URL.revokeObjectURL(url);
    }

    async function load() {
        try {
            const values = await Promise.all([
                E.fetchJSON('GetCampsServlet'),
                E.fetchJSON('GetMonitoringServlet'),
                E.fetchJSON('GetGrazingServlet'),
                E.fetchJSON('GetWaterServlet')
            ]);
            allData = { camps: values[0] || [], monitoring: values[1] || [], grazing: values[2] || [], water: values[3] || [] };
            populateCampFilter(allData.camps);
            render();
        } catch (error) {
            const target = document.getElementById('reportStatus');
            if (target) E.showAlert(target, 'error', error.message || 'Unable to load report data.');
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('.tab-btn').forEach(function (button) {
            button.addEventListener('click', function () { switchTab(button.getAttribute('data-tab')); });
        });
        ['filterCamp', 'filterFrom', 'filterTo'].forEach(function (id) {
            const node = document.getElementById(id);
            if (node) node.addEventListener('change', render);
        });
        document.getElementById('clearFilters').addEventListener('click', function () {
            document.getElementById('filterCamp').value = '';
            document.getElementById('filterFrom').value = '';
            document.getElementById('filterTo').value = '';
            render();
        });
        document.querySelectorAll('[data-export]').forEach(function (button) {
            button.addEventListener('click', function () { exportCSV(button.getAttribute('data-export')); });
        });
        document.getElementById('printReport').addEventListener('click', function () { window.print(); });
        switchTab('camps');
        load();
    });
})();
