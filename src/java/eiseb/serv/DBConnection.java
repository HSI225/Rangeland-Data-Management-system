package eiseb.serv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central JDBC connection factory.
 *
 * Prefer EISEB_DB_URL, EISEB_DB_USER and EISEB_DB_PASSWORD environment
 * variables in shared or production environments. The localhost fallback
 * keeps the original development workflow unchanged.
 */
public final class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/eiseb_rangeland?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            System.err.println("[Eiseb] MySQL JDBC driver not found. Make sure "
                    + "WEB-INF/lib/mysql-connector-j-9.3.0.jar is deployed.");
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = valueOrDefault(System.getenv("EISEB_DB_URL"), DEFAULT_URL);
        String user = valueOrDefault(System.getenv("EISEB_DB_USER"), DEFAULT_USER);
        String password = valueOrDefault(System.getenv("EISEB_DB_PASSWORD"), DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean isDuplicateKey(SQLException exception) {
        return "23000".equals(exception.getSQLState()) || exception.getErrorCode() == 1062;
    }

    /** True when the database server could not be reached or rejected the credentials/schema name. */
    public static boolean isConnectionProblem(SQLException exception) {
        String state = exception.getSQLState();
        int code = exception.getErrorCode();
        return (state != null && state.startsWith("08"))
                || code == 1045 || code == 1049 || code == 0;
    }

    private static String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
