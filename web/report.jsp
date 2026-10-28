<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Eiseb Rangeland Manager operational reports.">
    <title>Reports · Eiseb Rangeland Manager</title>
    <link rel="icon" type="image/svg+xml" href="assets/img/logo.svg">
    <link rel="stylesheet" href="assets/css/app.css">
</head>
<body>
<div class="app-shell">
    <aside class="sidebar">
        <a class="sidebar-brand" href="dashboard.jsp" aria-label="Eiseb dashboard">
            <span class="brand-mark small"><img src="assets/img/logo.svg" alt=""></span>
            <span class="brand-title">Eiseb <small>Rangeland Manager</small></span>
        </a>
        <nav class="sidebar-nav" aria-label="Main navigation">
            <a href="dashboard.jsp"><span class="nav-icon" aria-hidden="true">◼</span>Dashboard</a>
            <a href="camps.html"><span class="nav-icon" aria-hidden="true">⌂</span>Camps</a>
            <a href="grazing.html"><span class="nav-icon" aria-hidden="true">↻</span>Grazing</a>
            <a href="monitoring.html"><span class="nav-icon" aria-hidden="true">♧</span>Monitoring</a>
            <a href="water.html"><span class="nav-icon" aria-hidden="true">◌</span>Water</a>
            <a href="report.jsp"><span class="nav-icon" aria-hidden="true">▦</span>Reports</a>
        </nav>
        <div class="sidebar-footer">
            <form method="post" action="LogoutServlet">
                <button class="logout-button" type="submit"><span class="nav-icon" aria-hidden="true">⏻</span>Sign out</button>
            </form>
        </div>
    </aside>
    <div class="main-panel">
        <header class="topbar">
            <div>
                <p class="eyebrow">Management reporting</p>
                <h1>Operational reports</h1>
                <p>Review filtered records, print the management pack or export data as CSV.</p>
            </div>
            <div class="topbar-actions"><button id="printReport" class="btn btn-primary" type="button">Print report</button></div>
        </header>
        <main class="content">
            <div id="reportStatus" class="alerts" hidden></div>
            <section class="panel">
                <div class="panel-header"><div><h2>Report filters</h2><p><strong id="reportCount">0</strong> matching records.</p></div></div>
                <div class="panel-body filter-bar">
                    <label class="field"><span>Camp</span><select id="filterCamp"></select></label>
                    <label class="field"><span>From</span><input type="date" id="filterFrom"></label>
                    <label class="field"><span>To</span><input type="date" id="filterTo"></label>
                    <button id="clearFilters" class="btn btn-secondary" type="button">Clear filters</button>
                </div>
            </section>
            <section class="panel">
                <div class="panel-header"><div><h2>Report sections</h2><p>Choose a section or print all sections.</p></div></div>
                <div class="panel-body tabs">
                    <button class="btn tab-btn active" type="button" data-tab="camps">Camps</button>
                    <button class="btn tab-btn" type="button" data-tab="monitoring">Monitoring</button>
                    <button class="btn tab-btn" type="button" data-tab="grazing">Grazing</button>
                    <button class="btn tab-btn" type="button" data-tab="water">Water</button>
                </div>
            </section>
            <section id="tab-camps" class="panel report-tab active">
                <div class="panel-header"><div><h2>Camp register</h2><p>Land, capacity and infrastructure summary.</p></div><button class="btn btn-secondary" type="button" data-export="camps">Export CSV</button></div>
                <div class="table-wrap"><table id="campTable"><thead><tr><th>#</th><th>Camp</th><th>Location</th><th>Size</th><th>Capacity</th><th>Type</th><th>Water</th><th>Status</th></tr></thead><tbody></tbody></table></div>
            </section>
            <section id="tab-monitoring" class="panel report-tab">
                <div class="panel-header"><div><h2>Vegetation monitoring</h2><p>Condition and grass height observations.</p></div><button class="btn btn-secondary" type="button" data-export="monitoring">Export CSV</button></div>
                <div class="table-wrap"><table id="monitorTable"><thead><tr><th>#</th><th>Camp</th><th>Date</th><th>Cover</th><th>Grass height</th><th>Recorded by</th><th>Notes</th></tr></thead><tbody></tbody></table></div>
            </section>
            <section id="tab-grazing" class="panel report-tab">
                <div class="panel-header"><div><h2>Grazing rotations</h2><p>Livestock movement history and counts.</p></div><button class="btn btn-secondary" type="button" data-export="grazing">Export CSV</button></div>
                <div class="table-wrap"><table id="grazingTable"><thead><tr><th>#</th><th>Camp</th><th>Livestock</th><th>Count</th><th>Start</th><th>End</th><th>Notes</th></tr></thead><tbody></tbody></table></div>
            </section>
            <section id="tab-water" class="panel report-tab">
                <div class="panel-header"><div><h2>Water sources</h2><p>Capacity, inspection and condition records.</p></div><button class="btn btn-secondary" type="button" data-export="water">Export CSV</button></div>
                <div class="table-wrap"><table id="waterTable"><thead><tr><th>#</th><th>Camp</th><th>Source</th><th>Capacity</th><th>Last inspected</th><th>Condition</th></tr></thead><tbody></tbody></table></div>
            </section>
        </main>
        <p class="page-footer">© 2026 Eiseb Country Traders. Rangeland operations management.</p>
    </div>
</div>
<script src="assets/js/app.js"></script>
<script src="assets/js/reports.js"></script>
</body>
</html>
