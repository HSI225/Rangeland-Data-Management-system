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

@WebServlet("/MonitoringServlet")
public class MonitoringServlet extends HttpServlet {

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
            LocalDate monitorDate = ServletUtil.date(request.getParameter("monitor_date"), "Monitoring date");
            String cover = ServletUtil.trim(request.getParameter("vegetation_cover"));
            double grassHeight = ServletUtil.nonNegativeDouble(request.getParameter("grass_height_cm"), "Grass height");
            String recordedBy = ServletUtil.trim(request.getParameter("recorded_by"));
            String notes = ServletUtil.trim(request.getParameter("notes"));

            if (ServletUtil.isBlank(cover) || ServletUtil.isBlank(recordedBy)) {
                ServletUtil.redirect(response, "monitoring.html?error=validation");
                return;
            }

            String sql = "INSERT INTO vegetation_monitoring "
                    + "(camp_id, monitor_date, vegetation_cover, grass_height_cm, recorded_by, notes) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, campId);
                statement.setDate(2, Date.valueOf(monitorDate));
                statement.setString(3, cover);
                statement.setDouble(4, grassHeight);
                statement.setString(5, recordedBy);
                statement.setString(6, notes);
                statement.executeUpdate();
            }
            ServletUtil.redirect(response, "monitoring.html?success=added");
        } catch (IllegalArgumentException exception) {
            ServletUtil.redirect(response, "monitoring.html?error=validation");
        } catch (SQLException exception) {
            log("Unable to save monitoring record", exception);
            ServletUtil.redirect(response, "monitoring.html?error=database");
        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.positiveInt(request.getParameter("id"), "Monitoring ID");
            String sql = "DELETE FROM vegetation_monitoring WHERE monitor_id = ?";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
            ServletUtil.redirect(response, "monitoring.html?success=deleted");
        } catch (IllegalArgumentException exception) {
            ServletUtil.redirect(response, "monitoring.html?error=validation");
        } catch (SQLException exception) {
            log("Unable to delete monitoring record", exception);
            ServletUtil.redirect(response, "monitoring.html?error=database");
        }
    }
}
