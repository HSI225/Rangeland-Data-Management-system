<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Eiseb Rangeland Manager operational dashboard.">
    <title>Dashboard · Eiseb Rangeland Manager</title>
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
            <a class="${sessionScope.role eq 'admin' or sessionScope.role eq 'manager' ? '' : 'hidden'}" href="report.jsp"><span class="nav-icon" aria-hidden="true">▦</span>Reports</a>
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
                <p class="eyebrow">Operations overview</p>
                <h1>Good day, ${sessionScope.fullname}.</h1>
                <p>Monitor rangeland condition, grazing movement and water readiness from one place.</p>
            </div>
            <div class="topbar-actions">
                <span class="user-chip"><span class="avatar">${sessionScope.role}</span>${sessionScope.role}</span>
            </div>
        </header>
        <main class="content">
            <div id="dashboardStatus" class="alerts" hidden></div>
            <section class="stats-grid" aria-label="Key operational counts">
                <article class="card stat-card"><span class="stat-label">Registered camps</span><strong class="stat-value" id="statCamps">—</strong><span class="stat-hint">Total camp records</span></article>
                <article class="card stat-card"><span class="stat-label">Monitoring checks</span><strong class="stat-value" id="statMonitoring">—</strong><span class="stat-hint" id="latestMonitoring">—</span></article>
                <article class="card stat-card"><span class="stat-label">Grazing rotations</span><strong class="stat-value" id="statGrazing">—</strong><span class="stat-hint">Planned and active movements</span></article>
                <article class="card stat-card"><span class="stat-label">Water sources</span><strong class="stat-value" id="statWater">—</strong><span class="stat-hint" id="latestWater">—</span></article>
            </section>
            <section class="two-column">
                <div class="content">
                    <article class="panel">
                        <div class="panel-header"><div><h2>Vegetation condition</h2><p>Distribution of latest monitoring observations.</p></div></div>
                        <div class="panel-body"><div id="vegetationSummary" class="summary-list"></div></div>
                    </article>
                    <article class="panel">
                        <div class="panel-header"><div><h2>Water readiness</h2><p>Current source condition summary.</p></div></div>
                        <div class="panel-body"><div id="waterSummary" class="summary-list"></div></div>
                    </article>
                </div>
                <aside class="panel">
                    <div class="panel-header"><div><h2>Quick actions</h2><p>Capture and review field records.</p></div></div>
                    <div class="panel-body quick-grid">
                        <a class="quick-link" href="camps.html"><span>Add or review camps</span><strong>→</strong></a>
                        <a class="quick-link" href="grazing.html"><span>Plan grazing rotations</span><strong>→</strong></a>
                        <a class="quick-link" href="monitoring.html"><span>Record vegetation checks</span><strong>→</strong></a>
                        <a class="quick-link" href="water.html"><span>Update water sources</span><strong>→</strong></a>
                        <a class="quick-link ${sessionScope.role eq 'admin' or sessionScope.role eq 'manager' ? '' : 'hidden'}" href="report.jsp"><span>Open management reports</span><strong>→</strong></a>
                    </div>
                </aside>
            </section>
            <section class="panel">
                <div class="panel-header"><div><h2>Field indicator</h2><p>Average grass height across all monitoring records.</p></div><strong id="averageHeight" class="stat-value">—</strong></div>
            </section>
        </main>
        <p class="page-footer">© 2026 Eiseb Country Traders. Rangeland operations management.</p>
    </div>
</div>
<script src="assets/js/app.js"></script>
<script src="assets/js/dashboard.js"></script>
</body>
</html>
