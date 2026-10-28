package eiseb.serv;

import java.io.IOException;
import java.time.LocalDate;
import jakarta.servlet.http.HttpServletResponse;

public final class ServletUtil {

    private ServletUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static int positiveInt(String value, String fieldName) {
        try {
            int parsed = Integer.parseInt(trim(value));
            if (parsed <= 0) {
                throw new IllegalArgumentException(fieldName + " must be greater than zero.");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a valid whole number.");
        }
    }

    public static int nonNegativeInt(String value, String fieldName) {
        try {
            int parsed = Integer.parseInt(trim(value));
            if (parsed < 0) {
                throw new IllegalArgumentException(fieldName + " cannot be negative.");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a valid whole number.");
        }
    }

    public static double positiveDouble(String value, String fieldName) {
        try {
            double parsed = Double.parseDouble(trim(value));
            if (parsed <= 0) {
                throw new IllegalArgumentException(fieldName + " must be greater than zero.");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    public static double nonNegativeDouble(String value, String fieldName) {
        try {
            double parsed = Double.parseDouble(trim(value));
            if (parsed < 0) {
                throw new IllegalArgumentException(fieldName + " cannot be negative.");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    public static LocalDate date(String value, String fieldName) {
        try {
            return LocalDate.parse(trim(value));
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException(fieldName + " must be a valid date.");
        }
    }

    public static void redirect(HttpServletResponse response, String location) throws IOException {
        response.sendRedirect(location);
    }
}
