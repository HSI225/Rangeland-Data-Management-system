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

@WebServlet("/GetMonitoringServlet")
public class GetMonitoringServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder("[");
        String sql = "SELECT m.monitor_id, m.monitor_date, m.vegetation_cover, m.grass_height_cm, m.recorded_by, m.notes, rc.camp_name "
                + "FROM vegetation_monitoring m JOIN rangeland_camp rc ON m.camp_id = rc.camp_id "
                + "ORDER BY m.monitor_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            boolean first = true;
            while (result.next()) {
                if (!first) json.append(',');
                json.append('{')
                    .append("\"id\":").append(result.getInt("monitor_id")).append(',')
                    .append("\"campName\":").append(JsonUtil.quote(result.getString("camp_name"))).append(',')
                    .append("\"date\":\"").append(JsonUtil.date(result, "monitor_date")).append("\",")
                    .append("\"cover\":").append(JsonUtil.quote(result.getString("vegetation_cover"))).append(',')
                    .append("\"grassHeight\":").append(result.getDouble("grass_height_cm")).append(',')
                    .append("\"recordedBy\":").append(JsonUtil.quote(result.getString("recorded_by"))).append(',')
                    .append("\"notes\":").append(JsonUtil.quote(result.getString("notes")))
                    .append('}');
                first = false;
            }
        } catch (SQLException exception) {
            log("Unable to load monitoring records", exception);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load monitoring records");
            return;
        }
        json.append(']');
        response.getWriter().write(json.toString());
    }
}
