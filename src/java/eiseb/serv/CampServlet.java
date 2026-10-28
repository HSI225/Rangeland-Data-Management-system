package eiseb.serv;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CampServlet")
public class CampServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = ServletUtil.trim(request.getParameter("action"));
        if ("delete".equalsIgnoreCase(action)) {
            delete(request, response);
        } else {
            insert(request, response);
        }
    }

    private void insert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String campName = ServletUtil.trim(request.getParameter("camp_name"));
            String location = ServletUtil.trim(request.getParameter("location"));
            double size = ServletUtil.positiveDouble(request.getParameter("size_hectares"), "Camp size");
            int capacity = ServletUtil.nonNegativeInt(request.getParameter("grazing_capacity"), "Grazing capacity");
            String type = ServletUtil.trim(request.getParameter("camp_type"));
            String water = ServletUtil.trim(request.getParameter("water_source"));

            if (ServletUtil.isBlank(campName) || ServletUtil.isBlank(location)
                    || ServletUtil.isBlank(type) || ServletUtil.isBlank(water)) {
                ServletUtil.redirect(response, "camps.html?error=validation");
                return;
            }

            String sql = "INSERT INTO rangeland_camp "
                    + "(camp_name, location, size_hectares, grazing_capacity, camp_type, water_source) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, campName);
                statement.setString(2, location);
                statement.setDouble(3, size);
                statement.setInt(4, capacity);
                statement.setString(5, type);
                statement.setString(6, water);
                statement.executeUpdate();
            }
            ServletUtil.redirect(response, "camps.html?success=added");
        } catch (IllegalArgumentException exception) {
            ServletUtil.redirect(response, "camps.html?error=validation");
        } catch (SQLException exception) {
            log("Unable to save camp", exception);
            ServletUtil.redirect(response, "camps.html?error=database");
        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int campId = ServletUtil.positiveInt(request.getParameter("id"), "Camp ID");
            String sql = "DELETE FROM rangeland_camp WHERE camp_id = ?";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, campId);
                statement.executeUpdate();
            }
            ServletUtil.redirect(response, "camps.html?success=deleted");
        } catch (IllegalArgumentException exception) {
            ServletUtil.redirect(response, "camps.html?error=validation");
        } catch (SQLException exception) {
            log("Unable to delete camp", exception);
            if ("23000".equals(exception.getSQLState())) {
                ServletUtil.redirect(response, "camps.html?error=in-use");
            } else {
                ServletUtil.redirect(response, "camps.html?error=database");
            }
        }
    }
}
