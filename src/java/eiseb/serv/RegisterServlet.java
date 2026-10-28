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

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_.-]{3,30}$";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String fullname = ServletUtil.trim(request.getParameter("fullname"));
        String username = ServletUtil.trim(request.getParameter("username"));
        String role = ServletUtil.trim(request.getParameter("role"));
        String password = ServletUtil.trim(request.getParameter("password"));
        String confirmPassword = ServletUtil.trim(request.getParameter("confirmpass"));

        if (ServletUtil.isBlank(fullname) || ServletUtil.isBlank(username)
                || ServletUtil.isBlank(password) || ServletUtil.isBlank(confirmPassword)) {
            ServletUtil.redirect(response, "register.html?error=empty");
            return;
        }
        if (!password.equals(confirmPassword)) {
            ServletUtil.redirect(response, "register.html?error=mismatch");

        }
        if (password.length() < 4) {
            ServletUtil.redirect(response, "register.html?error=weak");
           
        }
        if (!username.matches(USERNAME_PATTERN)) {
            ServletUtil.redirect(response, "register.html?error=username");
            
        }
        if (!("staff".equals(role) || "admin".equals(role) || "manager".equals(role))) {
            role = "staff";
        }

        try (Connection connection = DBConnection.getConnection()) {
            if (usernameExists(connection, username)) {
                ServletUtil.redirect(response, "register.html?error=exists");
                
            }

            String insert = "INSERT INTO users (full_name, username, password, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                statement.setString(1, fullname);
                statement.setString(2, username);
                statement.setString(3, PasswordUtil.hash(password.toCharArray()));
                statement.setString(4, role);

                if (statement.executeUpdate() > 0) {
                    ServletUtil.redirect(response, "login.html?registered=true");
                } else {
                    ServletUtil.redirect(response, "register.html?error=failed");
                }
            }
        } catch (SQLException exception) {
            log("Registration failed: SQLState=" + exception.getSQLState()
                    + " code=" + exception.getErrorCode(), exception);
            if (DBConnection.isDuplicateKey(exception)) {
                ServletUtil.redirect(response, "register.html?error=exists");
            } else if (DBConnection.isConnectionProblem(exception)) {
                ServletUtil.redirect(response, "register.html?error=connection");
            } else {
                ServletUtil.redirect(response, "register.html?error=failed");
            }
        }
    }

    private boolean usernameExists(Connection connection, String username) throws SQLException {
        String sql = "SELECT FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }
}
