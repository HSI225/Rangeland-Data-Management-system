package eiseb.serv;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/WaterServlet")
public class WaterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if ("delete".equalsIgnoreCase(ServletUtil.trim(request.getParameter("action")))) {
            delete(request, response);
        } else {
            insert(request, response);
        }
    }

    private void insert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int campId = ServletUtil.positiveInt(request.getParameter("camp_id"), "Camp");
            String sourceType = ServletUtil.trim(request.getParameter("source_type"));
            double capacity = ServletUtil.positiveDouble(request.getParameter("capacity_liters"), "Capacity");
            LocalDate inspected = ServletUtil.date(request.getParameter("last_inspected"), "Inspection date");
            String condition = ServletUtil.trim(request.getParameter("condition_status"));

            if (ServletUtil.isBlank(sourceType) || ServletUtil.isBlank(condition)) {
                ServletUtil.redirect(response, "water.html?error=validation");
                return;
            }

            String sql = "INSERT INTO water_source "
                    + "(camp_id, source_type, capacity_liters, last_inspected, condition_status) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, campId);
                statement.setString(2, sourceType);
                statement.setDouble(3, capacity);
                statement.setDate(4, Date.valueOf(inspected));
                statement.setString(5, condition);
                statement.executeUpdate();
            }
            ServletUtil.redirect(response, "water.html?success=added");
        } catch (IllegalArgumentException exception) {
            ServletUtil.redirect(response, "water.html?error=validation");
        } catch (SQLException exception) {
            log("Unable to save water source", exception);
            ServletUtil.redirect(response, "water.html?error=database");
        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.positiveInt(request.getParameter("id"), "Water source ID");
            String sql = "DELETE FROM water_source WHERE water_id = ?";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
            ServletUtil.redirect(response, "water.html?success=deleted");
        } catch (IllegalArgumentException exception) {
            ServletUtil.redirect(response, "water.html?error=validation");
        } catch (SQLException exception) {
            log("Unable to delete water source", exception);
            ServletUtil.redirect(response, "water.html?error=database");
        }
    }
}
