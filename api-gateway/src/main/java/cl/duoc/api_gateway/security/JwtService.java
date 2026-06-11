package cl.duoc.api_gateway.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
public class JwtService {
    private static final String ALGORITHM = "HmacSHA256";
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final String secret;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${security.jwt.secret:tienda-videojuegos-local-dev-secret-change-me-2026}") String secret) {
        this.objectMapper = objectMapper;
        this.secret = secret;
    }

    public JwtClaims validar(String token) {
        String[] parts = token.split("\\.");

        if (parts.length != 3) {
            throw new InvalidJwtException("Token con formato invalido");
        }

        String unsignedToken = parts[0] + "." + parts[1];
        String expectedSignature;

        try {
            expectedSignature = encode(sign(unsignedToken));
        } catch (GeneralSecurityException exception) {
            throw new InvalidJwtException("No se pudo validar la firma del token", exception);
        }

        if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidJwtException("Token con firma invalida");
        }

        Map<String, Object> claims = readPayload(parts[1]);
        long expiration = asLong(claims.get("exp"));

        if (Instant.now().getEpochSecond() >= expiration) {
            throw new InvalidJwtException("Token expirado");
        }

        return new JwtClaims(
                asLongObject(claims.get("usuarioId")),
                asString(claims.get("sub")),
                asString(claims.get("nombreUsuario")),
                asString(claims.get("rol")),
                expiration
        );
    }

    private Map<String, Object> readPayload(String payload) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(payload);
            return objectMapper.readValue(decoded, MAP_TYPE);
        } catch (Exception exception) {
            throw new InvalidJwtException("Token con payload invalido", exception);
        }
    }

    private byte[] sign(String content) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
        return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    private String encode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private long asLong(Object value) {
        Long number = asLongObject(value);
        if (number == null) {
            throw new InvalidJwtException("Token sin expiracion");
        }
        return number;
    }

    private Long asLongObject(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            return Long.parseLong(text);
        }
        return null;
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }
}
