package eiseb.serv;

import static eiseb.serv.ServletUtil.redirect;
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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = ServletUtil.trim(request.getParameter("username"));
        String password = ServletUtil.trim(request.getParameter("password"));

        if (ServletUtil.isBlank(username) || ServletUtil.isBlank(password)) {
            ServletUtil.redirect(response, "login.html?error=empty");
            
        }

        String sql = "SELECT user_id, username, full_name, role, password "
                + "FROM users WHERE username = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next() || !PasswordUtil.verify(password.toCharArray(), result.getString("password"))) {
                    ServletUtil.redirect(response, "login.html?error=invalid");
                    
                }

                HttpSession session = request.getSession();
                session.setAttribute("userId", result.getInt("user_id"));
                session.setAttribute("username", result.getString("username"));
                session.setAttribute("fullname", result.getString("full_name"));
                session.setAttribute("role", result.getString("role"));
                session.setMaxInactiveInterval(30 * 60);

                if (PasswordUtil.needsUpgrade(result.getString("password"))) {
                    upgradeLegacyPassword(connection, result.getInt("user_id"), password);
                }
                
            }
        } catch (SQLException exception) {
            log("Login failed: SQLState=" + exception.getSQLState()
                    + " code=" + exception.getErrorCode(), exception);
            ServletUtil.redirect(response, DBConnection.isConnectionProblem(exception)
                    ? "login.html?error=connection" : "login.html?error=database");
        }
     
    }

    private void upgradeLegacyPassword(Connection connection, int userId, String password)
            throws SQLException {
        String update = "UPDATE users SET password = ? WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setString(1, PasswordUtil.hash(password.toCharArray()));
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }
}
