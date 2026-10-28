package eiseb.serv;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/GetReportSummaryServlet")
public class GetReportSummaryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String role = session == null ? "" : String.valueOf(session.getAttribute("role"));
        if (!("admin".equalsIgnoreCase(role) || "manager".equalsIgnoreCase(role))) {
            JsonUtil.writeError(response, HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (Connection connection = DBConnection.getConnection()) {
            String json = String.format(
                    "{\"camps\":%d,\"monitoring\":%d,\"grazing\":%d,\"water\":%d}",
                    count(connection, "rangeland_camp"),
                    count(connection, "vegetation_monitoring"),
                    count(connection, "grazing_rotation"),
                    count(connection, "water_source"));
            response.getWriter().write(json);
        } catch (SQLException exception) {
            log("Unable to build report summary", exception);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to build report summary");
        }
    }

    private int count(Connection connection, String table) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM " + table);
             ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt(1) : 0;
        }
    }
}
