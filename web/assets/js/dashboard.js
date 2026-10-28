(function () {
    'use strict';
    const E = window.Eiseb;

    function countBy(records, field) {
        return records.reduce(function (map, record) {
            const key = record[field] || 'Unknown';
            map[key] = (map[key] || 0) + 1;
            return map;
        }, {});
    }

    function renderSummary(id, values) {
        const target = document.getElementById(id);
        if (!target) return;
        const entries = Object.entries(values);
        const total = entries.reduce(function (sum, entry) { return sum + entry[1]; }, 0);
        if (!entries.length || total === 0) {
            target.innerHTML = '<p class="stat-hint">No records available.</p>';
            return;
        }
        target.innerHTML = entries.sort(function (a, b) { return b[1] - a[1]; }).map(function (entry) {
            const pct = Math.round((entry[1] / total) * 100);
            return '<div class="summary-item">'
                + '<div class="summary-row"><span>' + E.escapeHtml(entry[0]) + '</span><span>' + entry[1] + ' · ' + pct + '%</span></div>'
                + '<div class="progress"><span style="width:' + pct + '%"></span></div>'
                + '</div>';
        }).join('');
    }

    function latest(records, label) {
        if (!records.length) return 'No ' + label + ' records yet.';
        const record = records[0];
        if (record.date) return record.campName + ' · ' + E.formatDate(record.date);
        if (record.startDate) return record.campName + ' · ' + E.formatDate(record.startDate);
        if (record.lastInspected) return record.campName + ' · ' + E.formatDate(record.lastInspected);
        return record.name || record.campName || 'Record available';
    }

    async function load() {
        try {
            const data = await Promise.all([
                E.fetchJSON('GetCampsServlet'),
                E.fetchJSON('GetMonitoringServlet'),
                E.fetchJSON('GetGrazingServlet'),
                E.fetchJSON('GetWaterServlet')
            ]);
            const camps = data[0] || [];
            const monitoring = data[1] || [];
            const grazing = data[2] || [];
            const water = data[3] || [];

            E.setText('statCamps', E.formatNumber(camps.length));
            E.setText('statMonitoring', E.formatNumber(monitoring.length));
            E.setText('statGrazing', E.formatNumber(grazing.length));
            E.setText('statWater', E.formatNumber(water.length));

            const averageHeight = monitoring.length
                ? monitoring.reduce(function (sum, item) { return sum + Number(item.grassHeight || 0); }, 0) / monitoring.length
                : 0;
            E.setText('averageHeight', averageHeight ? E.formatNumber(averageHeight, 1) + ' cm' : '—');
            E.setText('latestMonitoring', latest(monitoring, 'monitoring'));
            E.setText('latestWater', latest(water, 'water'));

            renderSummary('vegetationSummary', countBy(monitoring, 'cover'));
            renderSummary('waterSummary', countBy(water, 'condition'));
        } catch (error) {
            const target = document.getElementById('dashboardStatus');
            if (target) E.showAlert(target, 'error', error.message || 'Unable to load dashboard data.');
        }
    }

    document.addEventListener('DOMContentLoaded', load);
})();
