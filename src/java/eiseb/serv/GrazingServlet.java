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

@WebServlet("/GrazingServlet")
public class GrazingServlet extends HttpServlet {

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
            String livestockType = ServletUtil.trim(request.getParameter("livestock_type"));
            int livestockCount = ServletUtil.positiveInt(request.getParameter("livestock_count"), "Livestock count");
            LocalDate start = ServletUtil.date(request.getParameter("start_date"), "Start date");
            LocalDate end = ServletUtil.date(request.getParameter("end_date"), "End date");
            String notes = ServletUtil.trim(request.getParameter("notes"));

            if (ServletUtil.isBlank(livestockType)) {
                ServletUtil.redirect(response, "grazing.html?error=validation");
                return;
            }
            if (end.isBefore(start)) {
                ServletUtil.redirect(response, "grazing.html?error=dates");
                return;
            }

            String sql = "INSERT INTO grazing_rotation "
                    + "(camp_id, livestock_type, livestock_count, start_date, end_date, notes) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, campId);
                statement.setString(2, livestockType);
                statement.setInt(3, livestockCount);
                statement.setDate(4, Date.valueOf(start));
                statement.setDate(5, Date.valueOf(end));
                statement.setString(6, notes);
                statement.executeUpdate();
            }
            ServletUtil.redirect(response, "grazing.html?success=added");
        } catch (IllegalArgumentException exception) {
            ServletUtil.redirect(response, "grazing.html?error=validation");
        } catch (SQLException exception) {
            log("Unable to save grazing rotation", exception);
            ServletUtil.redirect(response, "grazing.html?error=database");
        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.positiveInt(request.getParameter("id"), "Rotation ID");
            String sql = "DELETE FROM grazing_rotation WHERE rotation_id = ?";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
            ServletUtil.redirect(response, "grazing.html?success=deleted");
        } catch (IllegalArgumentException exception) {
            ServletUtil.redirect(response, "grazing.html?error=validation");
        } catch (SQLException exception) {
            log("Unable to delete grazing rotation", exception);
            ServletUtil.redirect(response, "grazing.html?error=database");
        }
    }
}
