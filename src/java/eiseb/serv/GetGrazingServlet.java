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

@WebServlet("/GetGrazingServlet")
public class GetGrazingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder("[");
        String sql = "SELECT gr.rotation_id, gr.livestock_type, gr.livestock_count, gr.start_date, gr.end_date, gr.notes, rc.camp_name "
                + "FROM grazing_rotation gr JOIN rangeland_camp rc ON gr.camp_id = rc.camp_id "
                + "ORDER BY gr.rotation_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            boolean first = true;
            while (result.next()) {
                if (!first) json.append(',');
                json.append('{')
                    .append("\"id\":").append(result.getInt("rotation_id")).append(',')
                    .append("\"campName\":").append(JsonUtil.quote(result.getString("camp_name"))).append(',')
                    .append("\"livestockType\":").append(JsonUtil.quote(result.getString("livestock_type"))).append(',')
                    .append("\"count\":").append(result.getInt("livestock_count")).append(',')
                    .append("\"startDate\":\"").append(JsonUtil.date(result, "start_date")).append("\",")
                    .append("\"endDate\":\"").append(JsonUtil.date(result, "end_date")).append("\",")
                    .append("\"notes\":").append(JsonUtil.quote(result.getString("notes")))
                    .append('}');
                first = false;
            }
        } catch (SQLException exception) {
            log("Unable to load grazing rotations", exception);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load grazing rotations");
            return;
        }
        json.append(']');
        response.getWriter().write(json.toString());
    }
}
