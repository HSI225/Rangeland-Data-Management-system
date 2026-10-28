package eiseb.serv;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Runs once at deployment. Makes sure the users table exists and is compatible
 * with the application (an older database often has a too-short password column,
 * which makes every registration fail because the PBKDF2 hash is ~110 characters).
 * Results are written to the server log so connection problems are easy to spot.
 */
@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "user_id INT UNSIGNED NOT NULL AUTO_INCREMENT, "
                    + "full_name VARCHAR(100) NOT NULL, "
                    + "username VARCHAR(30) NOT NULL, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "role ENUM('staff','manager','admin') NOT NULL DEFAULT 'staff', "
                    + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "PRIMARY KEY (user_id), UNIQUE KEY uq_users_username (username)"
                    + ") ENGINE=InnoDB");

            // Older tables: widen the password column so hashes fit.
            statement.executeUpdate("ALTER TABLE users MODIFY password VARCHAR(255) NOT NULL");

            DatabaseMetaData meta = connection.getMetaData();
            if (!hasColumn(meta, connection.getCatalog(), "role")) {
                statement.executeUpdate("ALTER TABLE users ADD COLUMN role "
                        + "ENUM('staff','manager','admin') NOT NULL DEFAULT 'staff'");
            }
            if (!hasColumn(meta, connection.getCatalog(), "full_name")) {
                statement.executeUpdate("ALTER TABLE users ADD COLUMN full_name VARCHAR(100) NOT NULL DEFAULT ''");
            }

            try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM users")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    statement.executeUpdate("INSERT INTO users (full_name, username, password, role) VALUES "
                            + "('System Administrator','admin','pbkdf2$120000$cRwkP/kHCQZgSaQt9ypelA==$392qW1R4yNl1gXlbvi4IeAaInaqInSsMwKR8YoHewv0=','admin'),"
                            + "('Operations Manager','manager','pbkdf2$120000$xMfgZ9mGOykLtEeA0n0i3g==$A3UH3NXJeqT+cJesrEPmJ+92oZev3QzHaw3OzJbTFPU=','manager'),"
                            + "('Field Staff','staff','pbkdf2$120000$Dg4Ee6RWpcFz/OLjprnekA==$4CNf8GurKU7JT1fKCUY+ImEvWeHQ5c49uWDTG8FImsw=','staff')");
                    event.getServletContext().log("[Eiseb] users table was empty - seeded default accounts.");
                }
            }
            event.getServletContext().log("[Eiseb] Database check OK (users table ready).");
        } catch (SQLException exception) {
            event.getServletContext().log("[Eiseb] DATABASE CHECK FAILED: " + exception.getMessage()
                    + " (SQLState=" + exception.getSQLState() + ", code=" + exception.getErrorCode()
                    + "). Verify MySQL is running, eiseb_rangeland exists, and the user/password in "
                    + "DBConnection / EISEB_DB_* are correct.", exception);
        }
    }

    private boolean hasColumn(DatabaseMetaData meta, String catalog, String column) throws SQLException {
        try (ResultSet rs = meta.getColumns(catalog, null, "users", column)) {
            return rs.next();
        }
    }
}
