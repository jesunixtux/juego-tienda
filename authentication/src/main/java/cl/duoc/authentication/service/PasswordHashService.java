package cl.duoc.authentication.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class PasswordHashService {
    private static final String ALGORITHM = "SHA-256";
    private static final String PREFIX = "sha256";
    private static final int SALT_BYTES = 16;

    private final SecureRandom secureRandom = new SecureRandom();

    public String hash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);
        return format(salt, digest(salt, password));
    }

    public boolean matches(String password, String storedHash) {
        if (password == null || storedHash == null || !storedHash.startsWith(PREFIX + "$")) {
            return false;
        }

        String[] parts = storedHash.split("\\$");
        if (parts.length != 3) {
            return false;
        }

        try {
            byte[] salt = Base64.getUrlDecoder().decode(parts[1]);
            byte[] expected = HexFormat.of().parseHex(parts[2]);
            byte[] actual = digest(salt, password);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private String format(byte[] salt, byte[] hash) {
        return PREFIX + "$"
                + Base64.getUrlEncoder().withoutPadding().encodeToString(salt)
                + "$"
                + HexFormat.of().formatHex(hash);
    }

    private byte[] digest(byte[] salt, String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
            messageDigest.update(salt);
            return messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("No se pudo generar hash de password", exception);
        }
    }
}
