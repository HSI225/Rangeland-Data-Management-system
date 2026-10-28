package eiseb.serv;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Password hashing using PBKDF2-HMAC-SHA256. */
public final class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(char[] password) {
        try {
            byte[] salt = new byte[SALT_LENGTH_BYTES];
            RANDOM.nextBytes(salt);
            byte[] hash = pbkdf2(password, salt, ITERATIONS);
            return "pbkdf2$" + ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to hash password", exception);
        }
    }

    public static boolean verify(char[] password, String storedValue) {
        if (storedValue == null || storedValue.isBlank()) {
            return false;
        }
        if (needsUpgrade(storedValue)) {
            // Compatibility path for the original plaintext development data.
            return MessageDigest.isEqual(
                    storedValue.getBytes(StandardCharsets.UTF_8),
                    new String(password).getBytes(StandardCharsets.UTF_8));
        }
        try {
            String[] parts = storedValue.split("\\$");
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = pbkdf2(password, salt, iterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            return false;
        }
    }

    public static boolean needsUpgrade(String storedValue) {
        return storedValue == null || !storedValue.startsWith("pbkdf2$");
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations)
            throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }
}
