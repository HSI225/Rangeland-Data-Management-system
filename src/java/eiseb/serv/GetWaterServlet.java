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

@WebServlet("/GetWaterServlet")
public class GetWaterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder("[");
        String sql = "SELECT ws.water_id, ws.source_type, ws.capacity_liters, ws.last_inspected, ws.condition_status, rc.camp_name "
                + "FROM water_source ws JOIN rangeland_camp rc ON ws.camp_id = rc.camp_id "
                + "ORDER BY ws.water_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            boolean first = true;
            while (result.next()) {
                if (!first) json.append(',');
                json.append('{')
                    .append("\"id\":").append(result.getInt("water_id")).append(',')
                    .append("\"campName\":").append(JsonUtil.quote(result.getString("camp_name"))).append(',')
                    .append("\"sourceType\":").append(JsonUtil.quote(result.getString("source_type"))).append(',')
                    .append("\"capacity\":").append(result.getDouble("capacity_liters")).append(',')
                    .append("\"lastInspected\":\"").append(JsonUtil.date(result, "last_inspected")).append("\",")
                    .append("\"condition\":").append(JsonUtil.quote(result.getString("condition_status")))
                    .append('}');
                first = false;
            }
        } catch (SQLException exception) {
            log("Unable to load water sources", exception);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load water sources");
            return;
        }
        json.append(']');
        response.getWriter().write(json.toString());
    }
}
