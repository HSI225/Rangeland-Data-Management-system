package eiseb.serv;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = {
    "/dashboard.jsp", "/report.jsp",
    "/camps.html", "/grazing.html", "/monitoring.html", "/water.html",
    "/CampServlet", "/GrazingServlet", "/MonitoringServlet", "/WaterServlet",
    "/GetCampsServlet", "/GetGrazingServlet", "/GetMonitoringServlet",
    "/GetWaterServlet", "/GetReportSummaryServlet"
})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        boolean authenticated = session != null && session.getAttribute("username") != null;

        if (!authenticated) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html");
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));
        String uri = httpRequest.getRequestURI();
        boolean managerReport = uri.endsWith("/report.jsp")
                || uri.endsWith("/GetReportSummaryServlet");
        if (managerReport && !("admin".equalsIgnoreCase(role) || "manager".equalsIgnoreCase(role))) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        addSecurityHeaders(httpResponse);
        chain.doFilter(request, response);
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "same-origin");
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; "
                + "img-src 'self' data:; connect-src 'self'; font-src 'self'; "
                + "object-src 'none'; base-uri 'self'; frame-ancestors 'none'");
    }
}
