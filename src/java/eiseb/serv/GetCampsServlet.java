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

@WebServlet("/GetCampsServlet")
public class GetCampsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder("[");
        String sql = "SELECT camp_id, camp_name, location, size_hectares, grazing_capacity, camp_type, water_source, status "
                + "FROM rangeland_camp ORDER BY camp_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            boolean first = true;
            while (result.next()) {
                if (!first) json.append(',');
                json.append('{')
                    .append("\"id\":").append(result.getInt("camp_id")).append(',')
                    .append("\"name\":").append(JsonUtil.quote(result.getString("camp_name"))).append(',')
                    .append("\"location\":").append(JsonUtil.quote(result.getString("location"))).append(',')
                    .append("\"size\":").append(result.getDouble("size_hectares")).append(',')
                    .append("\"capacity\":").append(result.getInt("grazing_capacity")).append(',')
                    .append("\"type\":").append(JsonUtil.quote(result.getString("camp_type"))).append(',')
                    .append("\"water\":").append(JsonUtil.quote(result.getString("water_source"))).append(',')
                    .append("\"status\":").append(JsonUtil.quote(result.getString("status")))
                    .append('}');
                first = false;
            }
        } catch (SQLException exception) {
            log("Unable to load camps", exception);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load camps");
            return;
        }
        json.append(']');
        response.getWriter().write(json.toString());
    }
}
